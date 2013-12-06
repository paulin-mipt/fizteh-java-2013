package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.*;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class NewTableProvider implements TableProvider, AutoCloseable {
    private File workingDirectory;
    private NewTable currentTable = null;
    private HashMap<String, NewTable> tables = new HashMap<>();
    private HashMap<String, Class<?>> providerTypes;
    private HashMap<Class<?>, String> providerTypesNames;
    private Lock providerController = new ReentrantLock(true);
    private CloseState state = new CloseState();

    public NewTableProvider(File dir) throws IOException {
        workingDirectory = dir;
        providerTypes = new HashMap<String, Class<?>>();
        providerTypesNames = new HashMap<Class<?>, String>();

        providerTypes.put("int", Integer.class);
        providerTypesNames.put(Integer.class, "int");
        providerTypes.put("long", Long.class);
        providerTypesNames.put(Long.class, "long");
        providerTypes.put("byte", Byte.class);
        providerTypesNames.put(Byte.class, "byte");
        providerTypes.put("float", Float.class);
        providerTypesNames.put(Float.class, "float");
        providerTypes.put("double", Double.class);
        providerTypesNames.put(Double.class, "double");
        providerTypes.put("boolean", Boolean.class);
        providerTypesNames.put(Boolean.class, "boolean");
        providerTypes.put("String", String.class);
        providerTypesNames.put(String.class, "String");
        for (File file : workingDirectory.listFiles()) {
            readTable(file);
        }
    }

    public Class<?> getNameClass(String str) {
        return providerTypes.get(str);
    }

    public String getNameString(Class<?> cl) {
        return providerTypesNames.get(cl);
    }

    public File getCurrentDirectory() {
        return workingDirectory;
    }

    public NewTable getNewCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(NewTable table) {
        currentTable = table;
    }

    private boolean checkNameOfDataBaseDirectory(String dir) {
        return (dir.matches("(([0-9])|(1[0-5]))\\.dir"));
    }

    private boolean checkNameOfFiles(String file) {
        return file.matches("(([0-9])|(1[0-5]))\\.dat");
    }

    @Override
    public Table getTable(String name) {
        state.checkClosed();
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("wrong type (Incorrect table name)");
        }
        providerController.lock();
        try {
            if (tables.containsKey(name)) {
                currentTable = tables.get(name);
                return currentTable;
            } else {
                File file = new File(workingDirectory, name);
                if (file.exists()) {
                    NewTable table;
                    try {
                        table = readTable(file);
                    } catch (IOException e) {
                        throw new IllegalStateException(e.getMessage(), e);
                    }
                    return table;
                } else {
                    return null;
                }
            }
        } finally {
            providerController.unlock();
        }
    }

    private NewTable readTable(File file) throws IOException {
        if (checkTableName(file.getName())) {
            if (file.isDirectory()) {
                tables.put(file.getName(), new NewTable(file.getName(), this));
                File tableFile = new File(workingDirectory, file.getName());
                HashMap<String, Storeable> map;
                try {
                    map = load(tableFile, file.getName());
                    tables.get(file.getName()).loadCommitedValues(map);
                    return tables.get(file.getName());
                } catch (ParseException e) {
                    throw new IOException(e.getMessage(), e);
                }
            } else {
                throw new IllegalArgumentException("not a directory");
            }
        } else {
            throw new IllegalArgumentException("wrong type (incorrect table name)");
        }

    }

    private HashMap<String, Storeable> load(File tableFile, String name) throws IOException, ParseException {
        HashMap<String, Storeable> map = new HashMap<String, Storeable>();
        for (File dir : tableFile.listFiles()) {
            if (checkNameOfDataBaseDirectory(dir.getName()) && dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (checkNameOfFiles(file.getName()) && file.isFile()) {
                        if (file.length() != 0) {
                            map.putAll(ReadDataBase.loadFile(file, new NewTable(name, this)));
                        }
                    }
                }
            }

        }
        return map;
    }

    private void removeTableFromDisk(NewTable table) {
        File tableFile = new File(workingDirectory, table.getName());
        for (File dir : tableFile.listFiles()) {
            if ((checkNameOfDataBaseDirectory(dir.getName()) && dir.isDirectory())
                    || (dir.getName().equals("signature.tsv"))) {
                if (dir.getName().equals("signature.tsv")) {
                    continue;
                }
                for (File file : dir.listFiles()) {
                    if (checkNameOfFiles(file.getName()) && file.isFile()) {
                        file.delete();
                    }
                }
                dir.delete();
            }
        }
    }

    public void saveChanges(NewTable table) throws IOException {
        currentTable = table;
        HashMap<File, HashMap<String, String>> files = makeFiles(table);
        providerController.lock();
        try {
            removeTableFromDisk(table);
            for (File file : files.keySet()) {
                File newDir = new File(workingDirectory + File.separator + table.getName() + File.separator
                        + file.getParentFile().getName());
                if (!newDir.exists()) {
                    if (!newDir.mkdirs()) {
                        throw new IOException("Can't create dir");
                    }
                }
                File newFile = new File(newDir, file.getName());
                WriteInDataBase.saveFile(newFile, files.get(file));
            }
        } finally {
            providerController.unlock();
        }
    }

    private HashMap<File, HashMap<String, String>> makeFiles(NewTable table) {
        HashMap<File, HashMap<String, String>> files = new HashMap<File, HashMap<String, String>>();
        HashMap<String, String> map = table.returnMap();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                File file = getFile(key);
                if (!files.containsKey(file)) {
                    files.put(file, new HashMap<String, String>());
                }
                files.get(file).put(key, map.get(key));
            }
        }
        return files;
    }

    private File getFile(String key) {
        byte c = 0;
        c = (byte) Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        int ndirectory = c % 16;
        int nfile = c / 16 % 16;
        File fileDir = new File(workingDirectory + File.separator + currentTable.getName() + File.separator
                + ndirectory + ".dir");
        File file = new File(fileDir, nfile + ".dat");
        return file;
    }

    private boolean checkTableName(String name) {
        return !((name == null) || (name.trim().isEmpty()) || (!name.matches("[a-zA-Z0-9а-яА-Я]+")));

    }

    @Override
    public void removeTable(String name) {
        state.checkClosed();
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("wrong type (Incorrect table name)");
        }
        providerController.lock();
        try {
            NewTable table = tables.remove(name);
            File tableFile = new File(workingDirectory, name);
            if (table == null) {
                throw new IllegalStateException(name + " not exists");
            } else {
                if (currentTable != null) {
                    if (table.getName().equals(currentTable.getName())) {
                        currentTable = null;
                    }
                }
                for (File dir : tableFile.listFiles()) {
                    if ((checkNameOfDataBaseDirectory(dir.getName()) && dir.isDirectory())
                            || (dir.getName().equals("signature.tsv"))) {
                        if (dir.getName().equals("signature.tsv")) {
                            dir.delete();
                            break;
                        } else {
                            for (File file : dir.listFiles()) {
                                if (checkNameOfFiles(file.getName()) && file.isFile()) {
                                    file.delete();
                                }
                            }
                            dir.delete();
                        }
                    }
                }
                tableFile.delete();
            }
        } finally {
            providerController.unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        state.checkClosed();
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("wrong type (Incorrect table name)");
        }
        if (columnTypes == null || !(checkValuesName(columnTypes)) || columnTypes.size() == 0) {
            throw new IllegalArgumentException("wrong type (Incorrect column name)");
        }
        providerController.lock();
        try {
            if (tables.get(name) != null) {
                return null;
            } else {
                File table = new File(workingDirectory, name);
                if (!table.exists()) {
                    if (!table.mkdir()) {
                        throw new IOException("Can't create dir");
                    }
                }
                File file = new File(workingDirectory + File.separator + name + File.separator + "signature.tsv");
                PrintStream newFile = new PrintStream(file);
                for (int i = 0; i < columnTypes.size(); ++i) {
                    if (columnTypes.get(i) == null) {
                        newFile.close();
                        throw new IllegalArgumentException("wrong type (Incorrect column name)");
                    }
                    newFile.print(getNameString(columnTypes.get(i)));
                    if (i != columnTypes.size() - 1) {
                        newFile.print(" ");
                    }
                }
                newFile.close();
            }
            tables.put(name, new NewTable(name, this));
            return tables.get(name);
        } finally {
            providerController.unlock();
        }
    }

    private boolean checkValuesName(List<Class<?>> columnTypes) {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>(columnTypes);
        for (Class<?> type : list) {
            if (type == null) {
                return false;
            } else {
                return type.equals(Integer.class) || type.equals(Long.class) || type.equals(Byte.class)
                        || type.equals(Float.class) || type.equals(Double.class) || type.equals(Boolean.class)
                        || type.equals(String.class);
            }
        }
        return false;
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        state.checkClosed();
        if (value == null || value.trim().isEmpty()) {
            throw new ParseException("Illegal value", 0);
        }
        JSONArray array = null;
        try {
            array = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("Incorrect format", 0);
        }
        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("Incorrect number of types", 0);
        }
        ArrayList<Object> values = new ArrayList<Object>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values.add(array.get(i));
        }
        try {
            return createFor(table, values);
        } catch (ColumnFormatException | IndexOutOfBoundsException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        state.checkClosed();
        if (value == null) {
            throw new ColumnFormatException("wrong type (Incorrect column name)");
        }
        return JSONSerializer.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        state.checkClosed();
        return new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        state.checkClosed();
        return new MyStoreable(table, values);
    }

    @Override
    public void close() throws Exception {
        providerController.lock();
        try {
            for (NewTable table : tables.values()) {
                table.close();
            }
            state.setClose();
        } finally {
            providerController.unlock();
        }

    }

    public void setClose(String name) {
        providerController.lock();
        try {
            tables.remove(name);
        } finally {
            providerController.unlock();
        }
    }

    @Override
    public String toString() {
        state.checkClosed();
        return this.getClass().getSimpleName() + "[" + workingDirectory.getAbsolutePath() + "]";
    }

}
