package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class UniversalDataTable<ValueType> implements AutoCloseable {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;
    protected final ReadWriteLock tableChangesLock = new ReentrantReadWriteLock();
    public ValueConverter<ValueType> valueConverter;
    protected File dataBaseDirectory;
    protected String tableName;
    protected boolean closed = false;
    private Map<String, ValueType> dataStorage = new HashMap<String, ValueType>();
    private ThreadLocal<Map<String, ValueType>> putKeys = new ThreadLocal<Map<String, ValueType>>() {
        @Override
        protected Map<String, ValueType> initialValue() {
            return new HashMap<String, ValueType>();
        }
    };
    private ThreadLocal<Set<String>> removeKeys = new ThreadLocal<Set<String>>() {
        @Override
        protected Set<String> initialValue() {
            return new HashSet<String>();
        }
    };

    public UniversalDataTable() {

    }

    public UniversalDataTable(String name) {
        tableName = name;
    }

    public UniversalDataTable(String name, File dir, ValueConverter converter) {
        tableName = name;
        dataBaseDirectory = dir;
        valueConverter = converter;
    }

    public String getName() {
        checkNotClosed();
        return tableName;
    }

    protected ValueType putSimple(String key, ValueType value) {
        checkNotClosed();
        ValueType oldValue = null;
        if (!removeKeys.get().contains(key)) {
            if ((oldValue = putKeys.get().get(key)) == null) {
                tableChangesLock.readLock().lock();
                try {
                    checkNotClosed();
                    oldValue = dataStorage.get(key);
                } finally {
                    tableChangesLock.readLock().unlock();
                }
                putKeys.get().put(key, value);
            } else {
                tableChangesLock.readLock().lock();
                ValueType dataValue;
                try {
                    checkNotClosed();
                    dataValue = dataStorage.get(key);
                } finally {
                    tableChangesLock.readLock().unlock();
                }
                if (dataValue == null) {
                    putKeys.get().put(key, value);
                } else {
                    tableChangesLock.readLock().lock();
                    try {
                        checkNotClosed();
                        if (!dataStorage.get(key).equals(value)) {
                            putKeys.get().put(key, value);
                        } else {
                            putKeys.get().remove(key);
                        }
                    } finally {
                        tableChangesLock.readLock().unlock();
                    }
                }

            }
        } else {
            tableChangesLock.readLock().lock();
            ValueType dataValue;
            try {
                checkNotClosed();
                dataValue = dataStorage.get(key);
            } finally {
                tableChangesLock.readLock().unlock();
            }
            if (!dataValue.equals(value)) {
                putKeys.get().put(key, value);
            }
            removeKeys.get().remove(key);
        }
        return oldValue;
    }

    public Set<String> getKeys() {
        return dataStorage.keySet();
    }

    public ValueType get(String key) throws IllegalArgumentException {
        checkNotClosed();
        if (key == null) {
            throw new IllegalArgumentException("Not correct key");
        }
        ValueType value = null;
        if (!putKeys.get().isEmpty()) {
            if (putKeys.get().containsKey(key)) {
                return putKeys.get().get(key);
            }
        }
        if (!removeKeys.get().contains(key)) {
            tableChangesLock.readLock().lock();
            try {
                checkNotClosed();
                value = dataStorage.get(key);
            } finally {
                tableChangesLock.readLock().unlock();
            }
            if (value == null) {
                value = putKeys.get().get(key);
            }
        }
        return value;
    }

    public ValueType remove(String key) throws IllegalArgumentException {
        checkNotClosed();
        if (key == null) {
            throw new IllegalArgumentException("Not correct key");
        }
        if (!putKeys.get().isEmpty()) {
            if (putKeys.get().get(key) != null) {
                tableChangesLock.readLock().lock();
                try {
                    checkNotClosed();
                    if (dataStorage.get(key) != null) {
                        removeKeys.get().add(key);
                    }
                } finally {
                    tableChangesLock.readLock().unlock();
                }
                return putKeys.get().remove(key);
            }
        }
        if (!removeKeys.get().isEmpty()) {
            if (removeKeys.get().contains(key)) {
                return null;
            }
        }
        ValueType value;
        tableChangesLock.readLock().lock();
        try {
            checkNotClosed();
            if ((value = dataStorage.get(key)) != null) {
                removeKeys.get().add(key);
            }
        } finally {
            tableChangesLock.readLock().unlock();
        }
        return value;
    }

    public boolean isEmpty() {
        tableChangesLock.readLock().lock();
        try {
            return dataStorage.isEmpty();
        } finally {
            tableChangesLock.readLock().unlock();
        }
    }

    public int size() {
        checkNotClosed();
        int size;
        tableChangesLock.readLock().lock();
        try {
            checkNotClosed();
            size = dataStorage.size();
        } finally {
            tableChangesLock.readLock().unlock();
        }
        Set<String> keysToCommit = putKeys.get().keySet();
        tableChangesLock.readLock().lock();
        try {
            checkNotClosed();
            for (String key : keysToCommit) {
                if (!dataStorage.containsKey(key)) {
                    ++size;
                }
            }
        } finally {
            tableChangesLock.readLock().unlock();
        }
        size -= removeKeys.get().size();
        return size;
    }

    protected int commitWithoutWriteToDataBase() {
        checkNotClosed();
        int commitSize = 0;
        if (!putKeys.get().isEmpty()) {
            Set<String> putKeysToCommit = putKeys.get().keySet();
            tableChangesLock.readLock().lock();
            try {
                checkNotClosed();
                for (String key : putKeysToCommit) {
                    if (dataStorage.get(key) == null) {
                        dataStorage.put(key, putKeys.get().get(key));
                        ++commitSize;
                    } else {
                        if (!valueConverter.convertValueTypeToString(dataStorage.get(key)).
                                equals(valueConverter.convertValueTypeToString(putKeys.get().get(key)))) {
                            dataStorage.put(key, putKeys.get().get(key));
                            ++commitSize;
                        }
                    }
                }
            } finally {
                tableChangesLock.readLock().unlock();
            }
            putKeys.get().clear();
        }
        if (!removeKeys.get().isEmpty()) {
            tableChangesLock.readLock().lock();
            try {
                checkNotClosed();
                for (String key : removeKeys.get()) {
                    dataStorage.remove(key);
                    ++commitSize;
                }
            } finally {
                tableChangesLock.readLock().unlock();
            }
            removeKeys.get().clear();
        }
        return commitSize;
    }

    public int rollback() {
        checkNotClosed();
        int rollbackSize = 0;
        if (!putKeys.get().isEmpty()) {
            Set<String> putKeysToRollback = putKeys.get().keySet();
            tableChangesLock.readLock().lock();
            try {
                checkNotClosed();
                for (String key : putKeysToRollback) {
                    if (dataStorage.get(key) == null) {
                        ++rollbackSize;
                    } else {
                        if (!valueConverter.convertValueTypeToString(dataStorage.get(key)).
                                equals(valueConverter.convertValueTypeToString(putKeys.get().get(key)))) {
                            ++rollbackSize;
                        }
                    }
                }
            } finally {
                tableChangesLock.readLock().unlock();
            }
            putKeys.get().clear();
        }
        if (!removeKeys.get().isEmpty()) {
            rollbackSize += removeKeys.get().size();
            removeKeys.get().clear();
        }
        return rollbackSize;
    }

    public int commitSize() {
        return putKeys.get().size() + removeKeys.get().size();
    }

    public File getWorkingDirectory() {
        return dataBaseDirectory;
    }

    protected void universalLoad() throws IOException, ParseException {
        File curTable = new File(dataBaseDirectory, tableName);
        curTable = curTable.getCanonicalFile();
        File[] dirs = curTable.listFiles();
        if (dirs.length > DIR_COUNT) {
            throw new IOException("The table includes more than " + DIR_COUNT + " directories");
        }
        for (File d : dirs) {
            if (!d.isDirectory() && (!d.getName().equals("signature.tsv"))) {
                throw new IOException(tableName + " should include only directories");
            }
            if (!d.getName().equals("signature.tsv")) {
                File[] files = d.listFiles();
                if ((files.length - 1) > FILE_COUNT) {
                    throw new IOException("The directory includes more than " + FILE_COUNT + " files");
                }
                String dirName = d.getName();
                char firstChar = dirName.charAt(0);
                char secondChar;
                int dirNumber;
                if (dirName.length() > 1) {
                    secondChar = dirName.charAt(1);
                } else {
                    throw new IllegalArgumentException("Not allowed name of directory in table");
                }
                if (Character.isDigit(firstChar)) {
                    if (Character.isDigit(secondChar)) {
                        dirNumber = Integer.parseInt(dirName.substring(0, 2));
                    } else {
                        dirNumber = Integer.parseInt(dirName.substring(0, 1));
                    }
                } else {
                    throw new IllegalArgumentException("Not allowed name of directory in table");
                }
                if (!dirName.equals(new String(dirNumber + ".dir"))) {
                    throw new IllegalArgumentException("Not allowed name of directory in table");
                }
                for (File f : files) {
                    if (!f.isFile()) {
                        throw new IOException("Unexpected directory");
                    }
                    String fileName = f.getName();
                    char firstFileChar = fileName.charAt(0);
                    char secondFileChar;
                    int fileNumber;
                    if (fileName.length() > 1) {
                        secondFileChar = fileName.charAt(1);
                    } else {
                        throw new IllegalArgumentException("Not allowed name of file in table");
                    }
                    if (Character.isDigit(firstFileChar)) {
                        if (Character.isDigit(secondFileChar)) {
                            fileNumber = Integer.parseInt(fileName.substring(0, 2));
                        } else {
                            fileNumber = Integer.parseInt(fileName.substring(0, 1));
                        }
                    } else {
                        throw new IllegalArgumentException("Not allowed name of file in table");
                    }
                    if (!fileName.equals(new String(fileNumber + ".dat"))) {
                        throw new IllegalArgumentException("Not allowed name of file in table");
                    }
                    FileReader fileReader = new FileReader(f, this);
                    while (fileReader.checkingLoadingConditions()) {
                        String key = fileReader.getNextKey();
                        int hashByte = Math.abs(key.getBytes()[0]);
                        int ndirectory = hashByte % DIR_COUNT;
                        int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                        if (ndirectory != dirNumber) {
                            throw new IllegalArgumentException("Wrong key in " + dirName);
                        }
                        if (fileNumber != nfile) {
                            throw new IllegalArgumentException("Wrong key in" + fileName);
                        }
                    }
                    while (fileReader.valuesToReadExists()) {
                        fileReader.putValueToTable(valueConverter.convertStringToValueType(fileReader.getNextValue()));
                    }
                    fileReader.closeResources();
                }
            }
        }
    }

    protected void writeToDataBaseWithoutSignature() throws IOException {
        putKeys.get().clear();
        removeKeys.get().clear();
        Set<String> keys = getKeys();
        if (!keys.isEmpty()) {
            for (int i = 0; i < DIR_COUNT; ++i) {
                File dir = new File(new File(dataBaseDirectory, tableName), new String(i + ".dir"));
                for (int j = 0; j < FILE_COUNT; ++j) {
                    DataTable keysToFile = new DataTable();
                    File file = new File(dir, new String(j + ".dat"));
                    for (String key : keys) {
                        int hashByte = Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
                        int ndirectory = hashByte % DIR_COUNT;
                        int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                        if ((ndirectory == i) && (nfile == j)) {
                            if (!dir.getCanonicalFile().exists()) {
                                dir.getCanonicalFile().mkdir();
                            }
                            if (!file.getCanonicalFile().exists()) {
                                file.getCanonicalFile().createNewFile();
                            }
                            keysToFile.put(key, valueConverter.convertValueTypeToString(get(key)));
                            keysToFile.commit();
                        }
                    }

                    if (!keysToFile.isEmpty()) {
                        FileWriter fileWriter = new FileWriter();
                        fileWriter.writeDataToFile(file.getCanonicalFile(), keysToFile);
                    } else {
                        if (file.getCanonicalFile().exists()) {
                            file.getCanonicalFile().delete();
                        }
                    }
                }
                if (dir.getCanonicalFile().listFiles() == null) {
                    dir.delete();
                }
            }
        }
    }

    public String toString() {
        checkNotClosed();
        File dataTable = new File(dataBaseDirectory, tableName);
        return new String(this.getClass().getSimpleName() + "[" + dataTable.toString() + "]");
    }

    public void close() throws IOException {
        if (!closed) {
            putKeys.get().clear();
            removeKeys.get().clear();
            closed = true;
        }
    }

    protected void checkNotClosed() {
        if (closed) {
            throw new IllegalStateException("the table is closed");
        }
    }

    public boolean isTableClosed() {
        return closed;
    }

    public abstract ValueType put(String key, ValueType value);

    public abstract int commit() throws IOException;

    public abstract void load() throws IOException, ParseException;

    public abstract void writeToDataBase() throws IOException;
}
