package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dzvonarev.shell.Remove;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTable implements Table, AutoCloseable {

    public MyTable(File dirTable, MyTableProvider currentProvider) throws IOException, RuntimeException {
        tableIsClosed = false;
        tableProvider = currentProvider;
        tableFile = dirTable;
        tableName = dirTable.getName();
        fileMap = new HashMap<>();
        changesMap = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
        type = new ArrayList<>();
        List<String> temp = new ArrayList<>();  //init types of table
        readTypes(temp);
        Parser myParser = new Parser();
        try {
            type = myParser.parseTypeList(temp);
        } catch (ParseException e) {
            throw new IOException(e);
        }

    }

    private String tableName;                      // name of current table
    private File tableFile;
    private MyTableProvider tableProvider;
    private HashMap<String, Storeable> fileMap;
    private ThreadLocal<HashMap<String, Storeable>> changesMap;
    private List<Class<?>> type;                   // types in this table
    private Lock readLock;
    private Lock writeLock;
    private volatile boolean tableIsClosed;

    public List<Class<?>> getTypeArray() {
        return type;
    }

    public int getCountOfChanges() throws IndexOutOfBoundsException {
        if (changesMap == null || changesMap.get().isEmpty()) {
            return 0;
        }
        Set<Map.Entry<String, Storeable>> fileSet = changesMap.get().entrySet();
        Iterator<Map.Entry<String, Storeable>> i = fileSet.iterator();
        int counter = 0;
        while (i.hasNext()) {
            Map.Entry<String, Storeable> currItem = i.next();
            Storeable value = currItem.getValue();
            if (value != null && !equals(value, fileMap.get(currItem.getKey()))
                    || value == null && fileMap.get(currItem.getKey()) != null) {
                ++counter;
            }
        }
        return counter;
    }

    public boolean equals(Storeable st1, Storeable st2) throws IndexOutOfBoundsException {
        for (int i = 0; i < getColumnsCount(); ++i) {
            if (st1 == null || st2 == null) {
                if (st1 == null && st2 == null) {
                    continue;
                }
                return false;
            }
            if (st1.getColumnAt(i) == null && st2.getColumnAt(i) == null) {
                continue;
            }
            if (st1.getColumnAt(i) == null && st2.getColumnAt(i) != null) {
                return false;
            }
            if (!st1.getColumnAt(i).equals(st2.getColumnAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void readTypes(List<String> arr) throws RuntimeException, IOException {
        File signature = new File(tableFile.getAbsolutePath(), "signature.tsv");
        if (!signature.exists()) {
            throw new RuntimeException("signature.tsv not existing");
        }
        try (Scanner myScanner = new Scanner(signature)) {
            if (!myScanner.hasNext()) {
                throw new RuntimeException("signature.tsv: invalid file");
            }
            while (myScanner.hasNext()) {
                arr.add(myScanner.next());
            }
        }
    }

    public void readFileMap() throws RuntimeException, IOException, ParseException {
        List<String> typeNames = new ArrayList<>();
        readTypes(typeNames);        // for storeable
        Parser myParser = new Parser();
        type = myParser.parseTypeList(typeNames);
        String[] dbDirs = tableFile.list();
        if (dbDirs != null && dbDirs.length != 0) {
            for (String dbDir : dbDirs) {
                if (dbDir.equals("signature.tsv")) {
                    continue;
                }
                if (!isValidDir(dbDir)) {
                    throw new RuntimeException("directory " + dbDir + " is not valid");
                }
                File dbDirTable = new File(tableFile.getAbsolutePath(), dbDir);
                String[] dbDats = dbDirTable.list();
                if (dbDats == null || dbDats.length == 0) {
                    throw new RuntimeException("table " + getName()
                            + " is not valid: directory " + dbDirTable + " is empty");
                }
                for (String dbDat : dbDats) {
                    String str = tableFile.getAbsolutePath() + File.separator + dbDir + File.separator + dbDat;
                    readMyFileMap(str, dbDir, dbDat);
                }
            }
        }
    }

    /* READING FILEMAP */
    public void readMyFileMap(String fileName, String dir, String file)
            throws IOException, RuntimeException, ParseException {
        try (RandomAccessFile fileReader = openFile(fileName)) {
            long endOfFile = fileReader.length();
            long currFilePosition = fileReader.getFilePointer();
            if (endOfFile == 0) {
                throw new RuntimeException("reading directory: " + dir + " is not valid");
            }
            while (currFilePosition != endOfFile) {
                int keyLen = fileReader.readInt();
                int valueLen = fileReader.readInt();
                if (keyLen <= 0 || valueLen <= 0) {
                    throw new RuntimeException(fileName + " : file is broken");
                }
                byte[] keyByte;
                byte[] valueByte;
                keyByte = new byte[keyLen];
                valueByte = new byte[valueLen];
                try {
                    fileReader.readFully(keyByte, 0, keyLen);
                    fileReader.readFully(valueByte, 0, valueLen);
                } catch (OutOfMemoryError e) {
                    throw new RuntimeException(e.getMessage() + " " + fileName + " : file is broken", e);
                }
                String key = new String(keyByte);
                String value = new String(valueByte);
                if (!keyIsValid(key, dir, file)) {
                    throw new RuntimeException("file " + file + " in " + dir + " is not valid");
                }
                Storeable storeable = tableProvider.deserialize(this, value);
                fileMap.put(key, storeable);
                currFilePosition = fileReader.getFilePointer();
                endOfFile = fileReader.length();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " " + fileName + " : file is broken", e);
        }
    }

    public boolean keyIsValid(String key, String dir, String file) {
        int b = key.getBytes()[0];
        int nDirectory = Math.abs(b) % 16;
        int nFile = Math.abs(b) / 16 % 16;
        String rightDir = nDirectory + ".dir";
        String rightFile = nFile + ".dat";
        return (dir.equals(rightDir) && file.equals(rightFile));
    }

    public boolean isFilesInDirValid(File file) {
        String[] files = file.list();
        if (files == null || files.length == 0) {
            return true;
        }
        for (String currFile : files) {
            if (new File(file.toString(), currFile).isDirectory()) {
                return false;
            }
            if (!currFile.matches("[0-9][.]dat|1[0-5][.]dat")) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidDir(String path) {
        File dir = new File(path);
        String[] file = dir.list();
        if (file == null || file.length == 0) {
            return true;
        }
        for (String currFile : file) {
            File newCurrFile = new File(path, currFile);
            if (newCurrFile.isDirectory() && currFile.matches("[0-9][.]dir|1[0-5][.]dir")) {
                if (!isFilesInDirValid(newCurrFile)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public void checkingValueForValid(Storeable value) throws ColumnFormatException {
        try {
            value.getColumnAt(getColumnsCount());   // to check, if value has more columns then types
            throw new ColumnFormatException("wrong type (invalid value " + value.getColumnAt(getColumnsCount()) + ")");
        } catch (IndexOutOfBoundsException e) {      // means that all ok
            for (int i = 0; i < getColumnsCount(); ++i) {
                try {
                    Parser myParser = new Parser();
                    if (!myParser.canBeCastedTo(type.get(i), value.getColumnAt(i))) {
                        throw new ColumnFormatException("wrong type (invalid value type in " + i + " column)");
                    }
                } catch (IndexOutOfBoundsException e1) {
                    throw new ColumnFormatException("wrong type (invalid value: it has less columns)");
                }
            }
        }
    }

    public void writeInTable() throws IOException {
        if (fileMap == null || fileMap.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, Storeable>> fileSet = fileMap.entrySet();
        for (Map.Entry<String, Storeable> currItem : fileSet) {
            String key = currItem.getKey();
            Storeable value = currItem.getValue();
            int b = key.getBytes()[0];
            int nDirectory = Math.abs(b) % 16;
            int nFile = Math.abs(b) / 16 % 16;
            String rightDir = nDirectory + ".dir";
            String rightFile = nFile + ".dat";
            String path = tableFile.getAbsolutePath() + File.separator + rightDir + File.separator + rightFile;
            String dir = tableFile.getAbsolutePath() + File.separator + rightDir;
            File file = new File(path);
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    throw new IOException("can't create directory " + dir);
                }
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException("can't create file " + path);
                }
            }
            writeInFile(path, key, value);
        }
    }

    public void writeInFile(String path, String key, Storeable value) throws IOException {
        try (RandomAccessFile fileWriter = openFile(path)) {
            fileWriter.skipBytes((int) fileWriter.length());
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = tableProvider.serialize(this, value).getBytes(StandardCharsets.UTF_8);
            fileWriter.writeInt(keyBytes.length);
            fileWriter.writeInt(valueBytes.length);
            fileWriter.write(keyBytes);
            fileWriter.write(valueBytes);
        } catch (IOException e) {
            throw new IOException(e.getMessage() + " updating file " + path + " : error in writing", e);
        }
    }

    public RandomAccessFile openFile(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException(e.getMessage() + " error in opening file: file " + fileName + " not found", e);
        }
        return newFile;
    }

    @Override
    public String getName() {
        checkTableIsClosed();
        return tableName.substring(tableName.lastIndexOf(File.separator) + 1, tableName.length());
    }

    public boolean containsWhitespace(String str) {
        for (int i = 0; i < str.length(); ++i) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        checkTableIsClosed();
        if (key == null || key.trim().isEmpty() || containsWhitespace(key)) {
            throw new IllegalArgumentException("wrong type (key " + key + " is not valid)");
        }
        if (changesMap.get().containsKey(key)) {            // если он был изменен
            return changesMap.get().get(key);
        } else {
            readLock.lock();
            try {
                if (fileMap.containsKey(key)) {
                    return fileMap.get(key);
                } else {
                    return null;
                }
            } finally {
                readLock.unlock();
            }
        }

    }

    public void addChanges(String key, Storeable value) {
        changesMap.get().put(key, value);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkTableIsClosed();
        if (key == null || key.trim().isEmpty() || containsWhitespace(key) || value == null) {
            throw new IllegalArgumentException("wrong type (key " + key + " is not valid or value)");
        }
        checkingValueForValid(value);
        writeLock.lock();
        try {
            Storeable oldValue = get(key);
            addChanges(key, value);
            return oldValue;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        checkTableIsClosed();
        if (key == null || key.trim().isEmpty() || containsWhitespace(key)) {
            throw new IllegalArgumentException("wrong type (key " + key + " is not valid)");
        }
        writeLock.lock();
        try {
            Storeable oldValue = get(key);
            if (oldValue != null) {
                addChanges(key, null);
            }
            return oldValue;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int size() throws IndexOutOfBoundsException {
        checkTableIsClosed();
        readLock.lock();
        try {
            return countSize() + fileMap.size();
        } finally {
            readLock.unlock();
        }
    }

    public void clearTable() throws IOException {
        String currentPath = tableFile.getAbsolutePath();
        String[] dirs = tableFile.list();
        for (String dir : dirs) {
            if (new File(currentPath, dir).isFile()) {
                continue;
            }
            Remove rm = new Remove();
            ArrayList<String> myArgs = new ArrayList<>();
            myArgs.add(currentPath + File.separator + dir);
            myArgs.add("notFromShell");
            rm.execute(myArgs);
            if (!(new File(currentPath, dir)).mkdir()) {
                throw new IOException("exit: can't make " + dir + " directory");
            }
        }
    }

    public void saveChangesOnHard() throws IOException {
        clearTable();
        writeInTable();
    }

    @Override
    public int commit() throws IndexOutOfBoundsException, IOException {
        checkTableIsClosed();
        writeLock.lock();
        int count;
        try {
            count = getCountOfChanges();
            if (count == 0) {
                changesMap.get().clear();
                return 0;
            }
            modifyFileMap();
            saveChangesOnHard();
        } finally {
            writeLock.unlock();
        }
        changesMap.get().clear();
        return count;
    }


    public void modifyFileMap() throws IndexOutOfBoundsException {
        if (changesMap == null || changesMap.get().isEmpty()) {
            return;
        }
        Set<Map.Entry<String, Storeable>> fileSet = changesMap.get().entrySet();
        for (Map.Entry<String, Storeable> currItem : fileSet) {
            Storeable value = currItem.getValue();
            if (!equals(value, fileMap.get(currItem.getKey()))) {
                if (value == null) {
                    fileMap.remove(currItem.getKey());
                } else {
                    fileMap.put(currItem.getKey(), value);
                }
            }
        }
    }

    public int countSize() throws IndexOutOfBoundsException {
        if (changesMap == null || changesMap.get().isEmpty()) {
            return 0;
        }
        int size = 0;
        Set<Map.Entry<String, Storeable>> fileSet = changesMap.get().entrySet();
        for (Map.Entry<String, Storeable> currItem : fileSet) {
            Storeable value = currItem.getValue();
            if (fileMap.get(currItem.getKey()) == null && value != null) {
                ++size;
            }
            if (fileMap.get(currItem.getKey()) != null && value == null) {
                --size;
            }
        }
        return size;
    }

    @Override
    public int rollback() throws IndexOutOfBoundsException {
        checkTableIsClosed();
        readLock.lock();
        int count;
        try {
            count = getCountOfChanges();
        } finally {
            readLock.unlock();
        }
        changesMap.get().clear();
        return count;
    }

    @Override
    public int getColumnsCount() {
        checkTableIsClosed();
        return type.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkTableIsClosed();
        if (columnIndex < 0 || columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index at " + columnIndex + ")");
        }
        return type.get(columnIndex);
    }

    @Override
    public String toString() {
        checkTableIsClosed();
        return MyTable.class.getSimpleName() + "[" + tableFile.getAbsolutePath() + "]";
    }

    @Override
    public void close() throws IndexOutOfBoundsException {
        if (tableIsClosed) {
            return;
        }
        rollback();
        tableIsClosed = true;
        writeLock.lock();
        try {
            tableProvider.removeClosedTable(tableName);
        } finally {
            writeLock.unlock();
        }
    }

    public void closeFromProvider() {
        if (tableIsClosed) {
            return;
        }
        rollback();
        tableIsClosed = true;
    }

    private void checkTableIsClosed() {
        if (tableIsClosed) {
            throw new IllegalStateException("table " + tableName + " is closed");
        }
    }

}
