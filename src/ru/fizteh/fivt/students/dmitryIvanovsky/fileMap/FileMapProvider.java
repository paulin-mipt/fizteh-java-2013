package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.belousova.utils.FileUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandAbstract;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.*;

public class FileMapProvider implements CommandAbstract, TableProvider, AutoCloseable {

    private final Path pathDb;
    private final CommandShell mySystem;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock read  = readWriteLock.readLock();
    private final Lock write = readWriteLock.writeLock();
    String useNameTable;
    Set<String> setDirTable;
    FileMap dbData;
    boolean out;
    boolean isProviderClose;
    Map<String, FileMap> mapFileMap = new HashMap<>();

    final HashSet allowType = new HashSet(){ {
        add(String.class);
        add(Boolean.class);
        add(Double.class);
        add(Float.class);
        add(Byte.class);
        add(Long.class);
        add(Integer.class);
    }};

    public Map<String, Object[]> mapComamnd() {
        Map<String, Object[]> commandList = new HashMap<String, Object[]>(){ {
            put("put",      new Object[] {"multiPut",      true,  2 });
            put("get",      new Object[] {"multiGet",      false, 1 });
            put("remove",   new Object[] {"multiRemove",   false, 1 });
            put("create",   new Object[] {"multiCreate",        true, 1 });
            put("drop",     new Object[] {"multiDrop",          false, 1 });
            put("use",      new Object[] {"multiUse",           false, 1 });
            put("size",     new Object[] {"multiSize",     false, 0 });
            put("commit",   new Object[] {"multiCommit",   false, 0 });
            put("rollback", new Object[] {"multiRollback", false, 0 });
        }};
        return commandList;
    }

    public FileMapProvider(String pathDb) throws Exception {
        this.out = true;
        this.useNameTable = "";
        this.pathDb = Paths.get(pathDb);
        this.mySystem = new CommandShell(pathDb, false, false);
        this.dbData = null;
        this.setDirTable = new HashSet<>();
        this.isProviderClose = false;

        try {
            checkBdDir(this.pathDb);
        } catch (Exception e) {
            e.addSuppressed(new ErrorFileMap("Error loading base"));
            throw e;
        }
    }

    private void checkBdDir(Path pathTables) throws Exception {
        File currentFile = pathTables.toFile();
        File[] listFiles = currentFile.listFiles();
        for (File nameMap : listFiles) {
            if (!nameMap.isDirectory()) {
                throw new ErrorFileMap(nameMap.getAbsolutePath() + " isn't directory");
            } else {
                setDirTable.add(nameMap.getName());
            }
        }
    }

    public String startShellString() {
        return "$ ";
    }

    public void multiPut(String[] args) throws ParseException {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            args = myParsing(args);
            String key = args[0];
            String value = args[1];
            Storeable res = dbData.put(key, deserialize(dbData, value));
            if (res == null) {
                outPrint("new");
            } else {
                outPrint("overwrite");
                outPrint(serialize(dbData, res));
            }
        }
    }

    public void multiGet(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            String key = args[0];
            Storeable res = dbData.get(key);
            if (res == null) {
                outPrint("not found");
            } else {
                String s = serialize(dbData, res);
                outPrint("found");
                outPrint(s);
            }
        }
    }

    public void multiRemove(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {

            String key = args[0];
            Storeable res = dbData.remove(key);
            if (res == null) {
                outPrint("not found");
            } else {
                outPrint("removed");
            }
        }
    }

    public void multiSize(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            outPrint(String.valueOf(dbData.size()));
        }
    }

    public void multiCommit(String[] args) {
        outPrint(String.valueOf(dbData.commit()));
    }

    public void multiRollback(String[] args) {
        outPrint(String.valueOf(dbData.rollback()));
    }

    public void multiUse(String[] args) throws Exception {
        String nameTable = args[0];
        if (!setDirTable.contains(nameTable)) {
            outPrint(nameTable + " not exists");
            return;
        }

        int changeKey = 0;
        if (dbData != null) {
            changeKey = dbData.changeKey();
        }

        if (changeKey > 0) {
            outPrint(String.format("%d unsaved changes", changeKey));
        } else {
            if (!nameTable.equals(useNameTable)) {
//                if (dbData != null) {
//                    //dbData.unloadTable();
//                }
                dbData = (FileMap) getTable(nameTable);
                useNameTable = nameTable;
            }
            outPrint("using " + nameTable);
        }
    }

    public void multiDrop(String[] args) throws ErrorFileMap {
        String nameTable = args[0];
        try {
            if (nameTable.equals(useNameTable)) {
                useNameTable = "";
            }
            removeTable(nameTable);
            outPrint("dropped");
        } catch (IllegalStateException e) {
            outPrint(nameTable + " not exists");
        }
    }

    public void multiCreate(String[] args) throws Exception {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        List<String> argsParse = parsingForCreate(args);
        String nameTable = argsParse.get(1);
        List<Class<?>> colType = new ArrayList<>();

        for (int i = 2; i < argsParse.size(); ++i) {
            Class<?> type = convertStringToClass(argsParse.get(i));
            if (type == null) {
                throw new IllegalArgumentException(String.format("wrong type (%s)", argsParse.get(i)));
            }
            colType.add(type);
        }
        Table fileMap = createTable(nameTable, colType);

        if (fileMap == null) {
            outPrint(nameTable + " exists");
            throw new ErrorFileMap(null);
        } else {
            outPrint("created");
        }
    }


    public Table createTable(String name, List<Class<?>> columnType) throws IOException {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is clear");
        }
        if (antiCorrectDir(name)) {
            throw new RuntimeException("bad symbol in name");
        }
        if (columnType == null || columnType.size() == 0) {
            throw new IllegalArgumentException("wrong type ( )");
        }
        for (Class<?> col : columnType) {
            if (col == null || !allowType.contains(col)) {
                throw new IllegalArgumentException("name is clear");
            }
        }

        Table resTable = null;
        write.lock();
        try {
            if (setDirTable.contains(name)) {
                resTable = null;
            } else {
                setDirTable.add(name);
                try {
                    FileMap fileMap = new FileMap(pathDb, name, this, columnType);
                    mapFileMap.put(name, fileMap);
                    resTable = fileMap;
                } catch (Exception e) {
                    RuntimeException error = new RuntimeException();
                    error.addSuppressed(e);
                    throw error;
                }
            }
        } finally {
            write.unlock();
        }
        return resTable;
    }

    public Table getTable(String name) {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is clear");
        }
        if (antiCorrectDir(name)) {
            throw new RuntimeException("bad symbol in name");
        }
        File currentFileMap = pathDb.resolve(name).toFile();
        if (!currentFileMap.isDirectory()) {
            return null;
        }

        Table resTable = null;
        write.lock();
        try {
            if (mapFileMap.containsKey(name)) {
                resTable = mapFileMap.get(name);
            } else {
                if (setDirTable.contains(name)) {
                    try {
                        FileMap fileMap = new FileMap(pathDb, name, this);
                        mapFileMap.put(name, fileMap);
                        resTable = fileMap;
                    } catch (Exception e) {
                        RuntimeException error = new RuntimeException();
                        error.addSuppressed(e);
                        throw error;
                    }
                } else {
                    resTable = null;
                }
            }
        } finally {
            write.unlock();
        }
        return resTable;
    }

    public void removeTable(String name) {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is clear");
        }

        write.lock();
        try {
            if (setDirTable.contains(name)) {
                setDirTable.remove(name);
                mapFileMap.remove(name);
                if (dbData != null) {
                    dbData.setDrop();
                }
                dbData = null;
                try {
                    mySystem.rm(new String[]{pathDb.resolve(name).toString()});
                } catch (Exception e) {
                    IllegalArgumentException ex = new IllegalArgumentException();
                    ex.addSuppressed(e);
                    throw ex;
                }
            } else {
                throw new IllegalStateException();
            }
        } finally {
            write.unlock();
        }

    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        try {
            List<Class<?>> columnTypes = new ArrayList<>();
            for (int i = 0; i < table.getColumnsCount(); i++) {
                columnTypes.add(table.getColumnType(i));
            }

            XMLInputFactory inputFactory = XMLInputFactory.newFactory();
            Storeable line = new FileMapStoreable(columnTypes);

            try {
                XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(value));
                try {
                    if (!reader.hasNext()) {
                        throw new ParseException("input string is empty", 0);
                    }
                    reader.nextTag();
                    if (!reader.getLocalName().equals("row")) {
                        throw new ParseException("invalid xml format", reader.getLocation().getCharacterOffset());
                    }
                    int columnIndex = 0;
                    while (reader.hasNext()) {
                        reader.nextTag();
                        if (!reader.getLocalName().equals("col")) {
                            if (!reader.getLocalName().equals("null") && !reader.isEndElement()) {
                                throw new ParseException("invalid xml", reader.getLocation().getCharacterOffset());
                            }
                            if (reader.isEndElement() && reader.getLocalName().equals("row")) {
                                break;
                            }
                        }
                        String text = reader.getElementText();

                        if (text.isEmpty()) {
                            if (reader.getLocalName().equals("null")) {
                                line.setColumnAt(columnIndex, null);
                            }
                        } else {
                            if (!text.equals("null")) {
                                line.setColumnAt(columnIndex, parseValue(text, columnTypes.get(columnIndex)));
                            }
                        }
                        columnIndex++;
                    }
                } finally {
                    reader.close();
                }
            } catch (XMLStreamException e) {
                throw new ParseException("xml reading error", 0);
            } catch (ColumnFormatException e) {
                throw new ParseException("xml reading error", 0);
            }
            return line;

        } catch (Exception e) {
            throw new ParseException("wrong data format", 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        List<Class<?>> columnType = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnType.add(table.getColumnType(i));
        }

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        StringWriter stringWriter = new StringWriter();
        try {
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stringWriter);
            try {
                writer.writeStartElement("row");
                for (int i = 0; i < columnType.size(); i++) {
                    if (value.getColumnAt(i) == null) {
                       writer.writeEmptyElement("null");
                    } else {
                        writer.writeStartElement("col");
                        writer.writeCharacters(getStringFromElement(value, i, columnType.get(i)));
                        writer.writeEndElement();
                    }
                }
                writer.writeEndElement();
            } finally {
                writer.close();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.closeStream(stringWriter);
        }
        return stringWriter.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        List<Class<?>> colType = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            colType.add(table.getColumnType(i));
        }
        return new FileMapStoreable(colType);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        Storeable storeable = new FileMapStoreable(columnTypes);
        int columnIndex = 0;
        for (Object value : values) {
            storeable.setColumnAt(columnIndex, value);
            ++columnIndex;
        }
        return storeable;
    }

    public String toString() {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        return String.format("%s[%s]", getClass().getSimpleName(), pathDb.toAbsolutePath());
    }

    public void closeTable(String table) {
        if (isProviderClose) {
            throw new IllegalStateException("provider is closed");
        }
        mapFileMap.remove(table);
    }

    public void close() {
        if (!isProviderClose) {
            for (FileMap table: mapFileMap.values()) {
                table.close();
            }
            mapFileMap.clear();
            isProviderClose = true;
        }
    }

    private void outPrint(String message) {
        if (out) {
            System.out.println(message);
        }
    }

}
