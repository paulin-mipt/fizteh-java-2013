package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseTableProvider implements TableProvider, AutoCloseable {
    public DatabaseTable curTable = null;
    HashMap<String, DatabaseTable> tables = new HashMap<String, DatabaseTable>();
    String curDir;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    volatile boolean isClosed;

    public DatabaseTableProvider(String directory) {
        isClosed = false;
        if (directory == null || directory.isEmpty()) {
            throw new IllegalArgumentException("Error with the property");
        }
        File databaseDirectory = new File(directory);
        curDir = directory;
        if (!open()) {
            throw new IllegalArgumentException("Wrong format");
        }
    }

    public String getDatabaseDirectory() {
        return curDir;
    }

    public DatabaseTable getTable(String name) throws IllegalArgumentException, IllegalStateException {
        isCloseChecker();

        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in suggested tablename " + name);
        }
        lock.writeLock().lock();
        try {
            if (!tables.containsKey(name)) {
                return null;
            }
            DatabaseTable table = tables.get(name);
            if (table.isClosed) {
                table = new DatabaseTable(table);
                tables.put(name, table);
            }

            if (table == null) {
                return table;
            }

            curTable = table;

            return table;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Table createTable(String name, List<Class<?>> columnTypes)
            throws IllegalArgumentException, IllegalStateException {
        isCloseChecker();
        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + name);
        }
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("column types cannot be null");
        }
        for (final Class<?> columnType : columnTypes) {
            if (columnType == null || ColumnTypes.fromTypeToName(columnType) == null) {
                throw new IllegalArgumentException("unknown column type");
            }
        }
        lock.writeLock().lock();
        try {
            File tableDirectory = new File(curDir, name);
            if (!tableDirectory.exists()) {
                tableDirectory.mkdir();
            }
            File signatureFile = new File(tableDirectory, "signature.tsv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(signatureFile))) {
                signatureFile.createNewFile();
                List<String> formattedColumnTypes = new ArrayList<String>();
                for (final Class<?> columnType : columnTypes) {
                    formattedColumnTypes.add(ColumnTypes.fromTypeToName(columnType));
                }
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (final Object listEntry : formattedColumnTypes) {
                    if (!first) {
                        sb.append(" ");
                    }
                    first = false;
                    if (listEntry == null) {
                        sb.append("null");
                    } else {
                        sb.append(listEntry.toString());
                    }
                }
                String signature = sb.toString();
                writer.write(signature);
            } catch (IOException e) {
                System.out.println("Can't write signature file to the disk");
                return null;
            }
            if (tables.containsKey(name)) {
                return null;
            }

            DatabaseTable table = new DatabaseTable(name, columnTypes, this);
            tables.put(name, table);
            return table;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static boolean recRemove(File file) throws IOException {
        if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                recRemove(innerFile);
            }
        }
        if (!file.delete()) {
            System.err.println("Error while deleting");
            return false;
        }
        return true;
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        isCloseChecker();
        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + name);
        }
        lock.writeLock().lock();
        try {
            if (!tables.containsKey(name)) {
                throw new IllegalStateException(String.format("%s not exists", name));
            }
            File temp = new File(curDir, name);
            if (temp.exists()) {
                File file = temp;
                try {
                    if (!recRemove(file)) {
                        System.err.println("File was not deleted");
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println(name + " not exists");
            }
            tables.remove(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Storeable deserialize(Table table, String value) throws ParseException {
        isCloseChecker();
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value cannot be null or empty");
        }
        Deserializer deserializer = new Deserializer(value);
        List<Class<?>> cols = new ArrayList<Class<?>>();
        Storeable result = new DatabaseStoreable(cols);
        List<Object> values = new ArrayList<>(table.getColumnsCount());
        for (int index = 0; index < table.getColumnsCount(); ++index) {
            try {
                Class<?> expectedType = table.getColumnType(index);
                Object columnValue = deserializer.getNext(expectedType);
                if (columnValue != null) {
                    switch (ColumnTypes.fromTypeToName(expectedType)) {
                        case "String":
                            String stringValue = (String) columnValue;
                            if (stringValue.trim().isEmpty()) {
                                throw new ParseException("value cannot be null", 0);
                            }
                            break;
                        default:
                    }
                }
                values.add(columnValue);
            } catch (ColumnFormatException e) {
                throw new ParseException("incompatible type: " + e.getMessage(), index);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Xml representation doesn't match the format", index);
            }
        }
        try {
            deserializer.close();
            result = createFor(table, values);
        } catch (ColumnFormatException e) {
            throw new ParseException("incompatible types: " + e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new ParseException("Xml representation doesn't match the format" + e.getCause(), 0);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return result;
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        isCloseChecker();
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        try {
            Serializer xmlSerializer = new Serializer();
            for (int index = 0; index < table.getColumnsCount(); ++index) {
                xmlSerializer.write(value.getColumnAt(index));
            }
            xmlSerializer.close();
            return xmlSerializer.getRepresentation();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            throw new IllegalArgumentException("incorrect value");
        }
        return null;
    }

    public Storeable createFor(Table table) {
        isCloseChecker();
        if (table == null) {
            return null;
        }
        List<Class<?>> columns = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columns.add(table.getColumnType(i));
        }
        DatabaseStoreable row = new DatabaseStoreable(columns);
        return row;
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        isCloseChecker();
        if (values == null) {
            throw new IllegalArgumentException("values cannot be null");
        }
        if (table == null) {
            return null;
        }
        List<Class<?>> columns = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columns.add(table.getColumnType(i));
        }
        DatabaseStoreable raw = new DatabaseStoreable(columns);
        raw.setColumns(values);
        return raw;
    }

    public File getDirWithNum(int dirNum) {
        String dirName = String.format("%d.dir", dirNum);
        File res = new File(curDir, curTable.getName());
        return new File(res, dirName);
    }

    public File getFileWithNum(int fileNum, int dirNum) {
        String dirName = String.format("%d.dir", dirNum);
        String fileName = String.format("%d.dat", fileNum);
        File res = new File(curDir, curTable.getName());
        res = new File(res, dirName);
        return new File(res, fileName);
    }

    public boolean open() {
        File databaseDirectory = new File(curDir);
        String curTableName;
        DatabaseTable loadingTable;
        for (File table : databaseDirectory.listFiles()) {
            curTableName = table.getName();
            List<Class<?>> zeroList = new ArrayList<Class<?>>();
            curTable = new DatabaseTable(curTableName, zeroList, this);
            loadingTable = new DatabaseTable(curTableName, zeroList, this);
            File preSignature = new File(curDir, curTableName);
            if (preSignature.listFiles().length == 0) {
                throw new IllegalArgumentException("Invalid database");
            }
            File signatureFile = new File(preSignature, "signature.tsv");
            String signature = null;
            if (!signatureFile.exists()) {
                throw new IllegalArgumentException("Invalid database");
            }
            if (signatureFile.length() == 0) {
                throw new IllegalArgumentException("Invalid database");
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(signatureFile))) {
                signature = reader.readLine();
            } catch (IOException e) {
                System.err.println("error loading signature file");
                throw new IllegalArgumentException("Invalid database");
            }
            List<Class<?>> columnTypes = new ArrayList<Class<?>>();
            for (final String columnType : signature.split("\\s")) {
                Class<?> type = ColumnTypes.fromNameToType(columnType);
                if (type == null) {
                    throw new IllegalArgumentException("unknown type");
                }
                columnTypes.add(type);
            }
            loadingTable.columnTypes = columnTypes;
            File[] files = new File(curDir, curTableName).listFiles();
            for (File step : files) {
                if (step.isFile()) {
                    continue;
                }
                if ((step.getName() == null) || (step.getName().isEmpty())) {
                    throw new IllegalArgumentException("Error with the property");
                }
            }
            if (files.length == 0) {
                tables.put(curTableName, loadingTable);
                continue;
            }
            for (int i = 0; i < 16; i++) {
                File currentDir = getDirWithNum(i);
                if (currentDir.isFile()) {
                    throw new IllegalArgumentException("Illegal argument: it is not a directory");
                }
                if (currentDir.exists() && currentDir.listFiles().length == 0) {
                    throw new IllegalArgumentException("Illegal database: the directory is empty");
                }
                if (!currentDir.exists()) {
                    continue;
                } else {
                    for (int j = 0; j < 16; ++j) {
                        File currentFile = getFileWithNum(j, i);
                        if (currentFile.exists()) {
                            try {
                                if (currentFile.length() == 0) {
                                    throw new IllegalArgumentException("Illegal database: empty file");
                                }
                                File tmpFile = new File(currentFile.toString());
                                RandomAccessFile temp = new RandomAccessFile(tmpFile, "r");
                                try {
                                    TableBuilder tableBuilder = new TableBuilder(this, loadingTable);
                                    loadTable(temp, loadingTable, i, j, tableBuilder);
                                } catch (EOFException e) {
                                    System.err.println("Wrong format");
                                    return false;
                                } catch (IOException e) {
                                    System.err.println("IO exception");
                                    return false;
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Wrong file format");
                                    return false;
                                } finally {
                                    temp.close();
                                }
                            } catch (IOException e) {
                                System.err.println("Cannot create new file");
                                return false;
                            }
                        }
                    }
                }
            }
            loadingTable.uncommittedChanges.set(0);
            tables.put(curTableName, loadingTable);
        }
        curTable = null;
        return true;
    }

    private void loadTable(RandomAccessFile temp, DatabaseTable table, int i, int j,
                           TableBuilder tableBuilder) throws IllegalArgumentException, IOException {
        if (temp.length() == 0) {
            return;
        }
        long nextOffset = 0;
        temp.seek(0);
        byte c = temp.readByte();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (c != 0) {
            out.write(c);
            c = temp.readByte();
        }
        String key = new String(out.toByteArray(), StandardCharsets.UTF_8);
        long firstOffset = temp.readInt();
        long currentOffset = firstOffset;
        long cursor = temp.getFilePointer();
        String nextKey = key;
        while (cursor < firstOffset) {
            c = temp.readByte();
            out = new ByteArrayOutputStream();
            while (c != 0) {
                out.write(c);
                c = temp.readByte();
            }
            nextKey = new String(out.toByteArray(), StandardCharsets.UTF_8);
            nextOffset = temp.readInt();
            cursor = temp.getFilePointer();
            temp.seek(currentOffset);
            int len = (int) (nextOffset - currentOffset);
            if (len < 0) {
                throw new IllegalArgumentException("File has incorrect format");
            }
            byte[] bytes = new byte[len];
            temp.read(bytes);
            String putValue = new String(bytes, StandardCharsets.UTF_8);
            if (i == DatabaseTable.getDirectoryNum(key) && j == DatabaseTable.getFileNum(key)) {
                tableBuilder.put(key, putValue);
            } else {
                throw new IllegalArgumentException("File has incorrect format");
            }
            temp.seek(cursor);
            key = nextKey;
            currentOffset = nextOffset;
        }
        temp.seek(currentOffset);
        int len = (int) (temp.length() - currentOffset);
        if (len < 0) {
            throw new IllegalArgumentException("File has incorrect format");
        }
        byte[] bytes = new byte[len];
        temp.read(bytes);
        String putValue = new String(bytes, StandardCharsets.UTF_8);
        if (i == DatabaseTable.getDirectoryNum(key) && j == DatabaseTable.getFileNum(key)) {
            tableBuilder.put(nextKey, putValue);
        } else {
            throw new IllegalArgumentException("File has incorrect format");
        }
    }

    public void isCloseChecker() {
        if (isClosed) {
            throw new IllegalStateException("It is closed");
        }
    }

    @Override
    public String toString() {
        isCloseChecker();
        return String.format("%s[%s]", getClass().getSimpleName(), curDir);
    }

    @Override
    public void close() throws Exception {
        if (isClosed) {
            return;
        }
        for (final String tableName : tables.keySet()) {
            tables.get(tableName).close();
        }
        isClosed = true;
    }
}
