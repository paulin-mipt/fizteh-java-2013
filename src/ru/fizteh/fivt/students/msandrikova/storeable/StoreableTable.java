package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class StoreableTable implements ChangesCountingTable, AutoCloseable {
    private boolean isClosed;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private StoreableTableProvider tableProvider;
    private String name;
    private File tablePath;
    private List<Class<?>> columnTypes = new ArrayList<Class<?>>();
    private Map<String, Storeable> originalDatabase = new HashMap<String, Storeable>();
    
    private static final int MAX_DIRECTORIES_AMOUNT = 16;
    private static final int MAX_DATABASES_IN_DIRECTORY_AMOUNT = 16;
    private static final int MAX_TABLE_SIZE = 1000 * 1000 * 100;
    private static final Pattern P = Pattern.compile("\\s+");
    public class TransactionHolder {
        private Map<String, Storeable> updates;
        private Set<String> removed;
        
        public TransactionHolder() {
            this.updates = new HashMap<String, Storeable>();
            this.removed = new HashSet<String>();
        }
        
        private boolean equalsStoreable(Storeable first, Storeable second) {
            String firstSer = StoreableTable.this.tableProvider.serialize(StoreableTable.this, first);
            String secondSer = StoreableTable.this.tableProvider.serialize(StoreableTable.this, second);
            return firstSer.equals(secondSer);
        }
        
        private void writeInFile(File currentFile, Set<String> keys) throws IOException {
            if (!currentFile.createNewFile()) {
                throw new IOException("Can not create file '" + currentFile.getName() + "'.");
            }
            DataOutputStream writer = null;
            try {
                writer = new DataOutputStream(new FileOutputStream(currentFile));
                String value;
                for (String key : keys) {
                    value = tableProvider.serialize(StoreableTable.this, 
                            StoreableTable.this.originalDatabase.get(key));
                    byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                    writer.writeInt(keyBytes.length);
                    writer.writeInt(valueBytes.length);
                    writer.write(keyBytes, 0, keyBytes.length);
                    writer.write(valueBytes, 0, valueBytes.length);
                }
            } finally {
                writer.close();
            }
        }
        
        private void write() throws IOException {
            File directory;
            for (int i = 0; i < MAX_DIRECTORIES_AMOUNT; ++i) {
                directory = new File(tablePath, i + ".dir");
                if (!directory.exists()) {
                    continue;
                }
                try {
                    Utils.remover(directory, "commit", false);
                } catch (IOException e) {
                    throw new IOException("Can not clean table direcory.");
                }
            }
            Map<Integer, Map<Integer, Set<String>>> 
                keysDueTheirHash = new HashMap<Integer, Map<Integer, Set<String>>>();
            for (int i = 0; i < StoreableTable.MAX_DIRECTORIES_AMOUNT; ++i) {
                keysDueTheirHash.put(i, new HashMap<Integer, Set<String>>());
                for (int j = 0; j < StoreableTable.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
                    keysDueTheirHash.get(i).put(j, new HashSet<String>());
                }
            }
            int dir;
            int dat;
            for (String key : StoreableTable.this.originalDatabase.keySet()) {
                dir = Utils.getNDirectory(key);
                dat = Utils.getNFile(key);
                keysDueTheirHash.get(dir).get(dat).add(key);
            }
            File currentFile;
            for (int i = 0; i < StoreableTable.MAX_DIRECTORIES_AMOUNT; ++i) {
                directory = new File(StoreableTable.this.tablePath, i + ".dir");
                for (int j = 0; j < StoreableTable.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
                    if (keysDueTheirHash.get(i).get(j).size() == 0) {
                        continue;
                    }
                    if (!directory.exists()) {
                        if (!directory.mkdir()) {
                            throw new IOException("Can not create directory '" 
                                    + i + ".dir'.");
                        }
                    }
                    currentFile = new File(directory, j + ".dat");
                    this.writeInFile(currentFile, keysDueTheirHash.get(i).get(j));
                }
            }
        }
        
        private boolean checkColumnTypes(Storeable value) {
            for (int i = 0; i < StoreableTable.this.getColumnsCount(); ++i) {
                try {
                    switch(StoreableTable.this.columnTypes.get(i).getName()) {
                    case "java.lang.Integer":
                        value.getIntAt(i);
                        break;
                    case "java.lang.Byte":
                        value.getByteAt(i);
                        break;
                    case "java.lang.Long":
                        value.getLongAt(i);
                        break;
                    case "java.lang.Float":
                        value.getFloatAt(i);
                        break;
                    case "java.lang.Boolean":
                        value.getBooleanAt(i);
                        break;
                    case "java.lang.String":
                        value.getStringAt(i);
                        break;
                    case "java.lang.Double":
                        value.getDoubleAt(i);
                        break;
                    default:
                        break;
                    }
                } catch (ColumnFormatException| IndexOutOfBoundsException e) {
                    return false;
                }
            }
            try {
                value.getColumnAt(StoreableTable.this.getColumnsCount());
            } catch (IndexOutOfBoundsException e) {
                return true;
            }
            return false;
        }
       
        public Storeable get(String key) throws IllegalArgumentException, IllegalStateException {
            StoreableTable.this.checkIsClosed();
            if (Utils.isEmpty(key)) {
                throw new IllegalArgumentException("Key can not be null");
            }
            
            if (this.updates.get(key) != null) {
                return this.updates.get(key);
            }
            
            if (this.removed.contains(key)) {
                return null;
            }
            
            return StoreableTable.this.originalDatabase.get(key);
        }
            
        public Storeable put(String key, Storeable value) 
                throws IllegalArgumentException, IllegalStateException {
            StoreableTable.this.checkIsClosed();
            if (Utils.isEmpty(key) || value == null || P.matcher(key).find()) {
                throw new IllegalArgumentException("Key and name can not be null or "
                        + "newline and key can not contain whitespace");
            }
            if (!this.checkColumnTypes(value)) {
                throw new ColumnFormatException("Incorrent column types in given storeable.");
            }
            Storeable oldValue = this.get(key);
            this.updates.put(key, value);
            this.removed.remove(key);
            return oldValue;
        }
        
        public Storeable remove(String key) throws IllegalArgumentException, IllegalStateException {
            StoreableTable.this.checkIsClosed();
            if (Utils.isEmpty(key)) {
                throw new IllegalArgumentException("Key can not be null");
            }
            
            Storeable removedValue = this.get(key);
            this.updates.remove(key);
            this.removed.add(key);
            
            return removedValue;
        }
        
        public int size() throws IllegalStateException {
            StoreableTable.this.checkIsClosed();
            
            int size = StoreableTable.this.originalDatabase.size();
            
            for (String key : this.updates.keySet()) {
                if (StoreableTable.this.originalDatabase.get(key) == null) {
                    ++size;
                }
            }
            
            for (String key : this.removed) {
                if (StoreableTable.this.originalDatabase.get(key) != null) {
                    --size;
                }
            }
            
            return size;
        }
        
        public int commit() throws IOException, IllegalStateException {
            StoreableTable.this.checkIsClosed();
            
            int changesCount = this.unsavedChangesCount();
            for (String key : this.updates.keySet()) {
                StoreableTable.this.originalDatabase.put(key, this.updates.get(key));
            }
            
            for (String key : this.removed) {
                StoreableTable.this.originalDatabase.remove(key);
            }
            this.removed.clear();
            this.updates.clear();
            this.write();
            return changesCount;
        }
        
        public int rollback() throws IllegalStateException {
            StoreableTable.this.checkIsClosed();
            
            int changesCount = this.unsavedChangesCount();
            this.removed.clear();
            this.updates.clear();
            return changesCount;
        }
        
        public int unsavedChangesCount() {
            int changesCount  = 0;
            for (String key : this.updates.keySet()) {
                if (StoreableTable.this.originalDatabase.get(key) == null 
                        || !this.equalsStoreable(this.updates.get(key), 
                                StoreableTable.this.originalDatabase.get(key))) {
                    ++changesCount;
                }
            }
            
            for (String key : this.removed) {
                if (StoreableTable.this.originalDatabase.get(key) != null) {
                    ++changesCount;
                }
            }
            return changesCount;
        }
    }
    
    private ThreadLocal<TransactionHolder> transaction;
    
    private boolean checkHash(int directory, int database, String key) {
        if (directory != Utils.getNDirectory(key) || database != Utils.getNFile(key)) {
            return false;
        }
        return true;
    }
    
    private void readFile(int directory, int database) throws IOException {
        File currentFile = new File(this.tablePath, directory + ".dir");
        if (!currentFile.isDirectory()) {
            throw new IOException("File '" + directory + ".dir' must be directory.");
        }
        currentFile = new File(currentFile, database + ".dat");
        if (!currentFile.isFile()) {
            throw new IOException("File '" + database + ".dat' in directory '" 
                    + directory + ".dir' can not be be directory.");
        }
        
        int keyLength;
        int valueLength;
        String key;
        String value;
        DataInputStream reader = null;
        int count = 0;
        try {
            reader = new DataInputStream(new FileInputStream(currentFile));
            while (true) {
                try {
                    keyLength = reader.readInt();
                } catch (EOFException e) {
                    if (count == 0) {
                        throw new IOException("empty dat");
                    }
                    break;
                }
                    ++count;
                    if (keyLength <= 0 || keyLength >= 1000 * 1000) {
                        throw new IOException("reader: Invalid key length.");
                    }
                    
                    valueLength = reader.readInt();
                    if (valueLength <= 0 || valueLength >= 1000 * 1000) {
                        throw new IOException("reader: Invalid value length.");
                    }
                    
                    byte[] keyByteArray = new byte[keyLength];
                    reader.read(keyByteArray, 0, keyLength);
                    key = new String(keyByteArray);
                    
                    byte[] valueByteArray = new byte[valueLength];
                    reader.read(valueByteArray, 0, valueLength);
                    value = new String(valueByteArray);
                    
                    if (!this.checkHash(directory, database, key)) {
                        throw new IOException("Key " + key + " can not be in '" 
                                + directory + ".dir/" + database + ".dat'.");
                    }
                    
                    try {
                        this.originalDatabase.put(key, this.tableProvider.deserialize(this, value));
                    } catch (ParseException e) {
                        throw new IOException(e.getMessage());
                    }
                    
                    if (this.originalDatabase.size() >= StoreableTable.MAX_TABLE_SIZE) {
                        throw new IOException("Table '" + this.name + "' is overly big.");
                    }
                
            }
        } finally {
            reader.close();
        }
        
    }
    
    private void writeSignature() throws IOException {
        File signature = new File(StoreableTable.this.tablePath, "signature.tsv");
        if (signature.exists()) {
            return;
        } 
        if (!signature.createNewFile()) {
            throw new IOException("Can not create 'signature.tsv' file.");
        }
        List<String> columnTypesNames = Utils.getColumnTypesNames(StoreableTable.this.columnTypes);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(signature));
            for (int i = 0; i < StoreableTable.this.getColumnsCount(); ++i) {
                writer.write(columnTypesNames.get(i));
                if (i != StoreableTable.this.getColumnsCount() - 1) {
                    writer.write(" ");
                }
            }
        } finally {
            writer.close();
        }
    }
    
    public StoreableTable(File dir, String name, List<Class<?>> columnTypes, 
            StoreableTableProvider tableProvider) throws IOException {
        this.name = name;
        this.isClosed = false;
        this.tableProvider = tableProvider;
        this.columnTypes = columnTypes;
        this.tablePath = new File(dir, name);
        if (!this.tablePath.exists()) {
            if (!this.tablePath.mkdir()) {
                throw new IOException("Can not create directory for table " + this.name);
            }
            this.writeSignature();
        } else {
            int i;
            for (i = 0; i < StoreableTable.MAX_DIRECTORIES_AMOUNT; ++i) {
                File directory = new File(this.tablePath, i + ".dir");
                if (!directory.exists()) {
                    continue;
                }
                int count = 0;
                for (int j = 0; j < StoreableTable.MAX_DATABASES_IN_DIRECTORY_AMOUNT; ++j) {
                    File currentFile = new File(directory, j + ".dat");
                    if (!currentFile.exists()) {
                        continue;
                    }
                    ++count;
                    this.readFile(i, j);
                }
                if (count == 0) {
                    throw new IOException("empty dir");
                }
            }
        }
        this.transaction = new ThreadLocal<TransactionHolder>() {
            @Override
            protected TransactionHolder initialValue() {
                return new TransactionHolder();
            }
        };
    }

    @Override
    public String getName() throws IllegalStateException { 
        this.checkIsClosed();
        
        lock.readLock().lock();
        String answer = StoreableTable.this.name;
        lock.readLock().unlock();
        return answer;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException, IllegalStateException {
        lock.readLock().lock();
        Storeable answer = null;
        try {
            answer = this.transaction.get().get(key);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException, IllegalStateException {
        lock.readLock().lock();
        Storeable answer = null;
        try {
            answer = this.transaction.get().put(key, value);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException, IllegalStateException {
        lock.readLock().lock();
        Storeable answer = null;
        try {
            answer = this.transaction.get().remove(key);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public int size() throws IllegalStateException {
        lock.readLock().lock();
        int answer = 0;
        try {
            answer = this.transaction.get().size();
        } catch (IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public int commit() throws IOException, IllegalStateException {
        lock.writeLock().lock();
        int answer = 0;
        try {
            answer = this.transaction.get().commit();
        } catch (IOException | IllegalStateException e) {
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
        return answer;
    }

    @Override
    public int rollback() throws IllegalStateException {
        lock.readLock().lock();
        int answer = 0;
        try {
            answer = this.transaction.get().rollback();
        } catch (IllegalStateException e) {
            throw e;
        } finally {
            lock.readLock().unlock();
        }
        return answer;
    }

    @Override
    public int getColumnsCount() throws IllegalStateException {
        StoreableTable.this.checkIsClosed();
        
        lock.readLock().lock();
        int answer = 0;
        answer = this.columnTypes.size();
        lock.readLock().unlock();
        return answer;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException, IllegalStateException {
        StoreableTable.this.checkIsClosed();
        
        lock.readLock().lock();
        Class<?> answer = null;
        
        if (columnIndex < 0 || columnIndex >= this.getColumnsCount()) {
            lock.readLock().unlock();
            throw new IndexOutOfBoundsException("Column index can not be less then "
                    + "0 and more then types amount.");
        }
        answer = this.columnTypes.get(columnIndex);
        lock.readLock().unlock();
        return answer;
    }

    @Override
    public int unsavedChangesCount() {
        return this.transaction.get().unsavedChangesCount();
    }
    
    private void checkIsClosed() throws IllegalStateException {
        this.lock.readLock().lock();
        if (this.isClosed) {
            this.lock.readLock().unlock();
            throw new IllegalStateException("Table was closed.");            
        }
        this.lock.readLock().unlock();
    }
    
    @Override
    public void close() throws IllegalStateException {
        if (this.isClosed) {
            return;
        }
        
        this.rollback();
        
        this.lock.writeLock().lock();
        this.isClosed = true;
        if (this.tablePath.exists()) {
            this.tableProvider.changeReference(this.tablePath, this.name);
        }
        this.lock.writeLock().unlock();    
    }
    
    @Override
    public String toString() throws IllegalStateException {
        this.checkIsClosed();
        
        String className = this.getClass().getSimpleName();
        String tablePath = this.tablePath.getAbsolutePath();
        
        return className + "[" + tablePath + "]";
    }
    
}
