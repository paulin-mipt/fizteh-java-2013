package ru.fizteh.fivt.students.elenav.storeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.utils.Writer;

public class StoreableTableState extends FilesystemState implements Table, AutoCloseable {

    private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;
    
    private boolean isClosed = false;
    private List<Class<?>> columnTypes = new ArrayList<>();
    private volatile WeakHashMap<String, Storeable> startMap 
                         = new WeakHashMap<String, Storeable>();
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final ThreadLocal<HashMap<String, Storeable>> changedKeys 
                         = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    private final ThreadLocal<HashSet<String>> removedKeys = new ThreadLocal<HashSet<String>>() {
        @Override
        protected HashSet<String> initialValue() {
            return new HashSet<String>();
        }
    };
    
    public StoreableTableState(String n, File wd, PrintStream out, StoreableTableProvider provider2) {
        super(n, wd, out);
        provider = provider2;
        try {
            if (wd != null) {
                getColumnTypes();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void clearTable() {
        startMap.clear();
    }
    
    @Override
    public String getName() {
        checkIsNotClosed();
        return super.getName();
    }
    
    private int getSize() {
        try {
            int result;
            File in = new File(getWorkingDirectory(), "size.tsv");
            if (!in.isFile()) {
                in.createNewFile();
                return setSize();
            } else {
                Scanner s = new Scanner(in);
                result = s.nextInt();
                s.close();
                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException("doesn't exist file size.tsv");
        }
    }
    
    private int setSize() {
        int size = 0;    
        for (int i = 0; i < DIR_COUNT; ++i) {
            for (int j = 0; j < FILES_PER_DIR; ++j) {
                File f = getFilePath(i, j);
                if (f.isFile() && f.length() != 0) {
                    try {
                        size += readFileForSize(f);
                    } catch (IOException e) {
                        throw new RuntimeException("setSize(): " + e.getMessage());
                    }
                }
            }
        }
        writeSize(size);
        return size;
    }

    private void writeSize(int size) {
        try {
            File in = new File(getWorkingDirectory(), "size.tsv");
            PrintStream s = new PrintStream(in);
            s.print(size);
            s.close();
        } catch (IOException e) {
            throw new RuntimeException("problems in writeSize()", e);
        }        
    }

    private Storeable getStartValue(String key) {
        Storeable result = startMap.get(key);
        if (result != null) {
            return result;
        } else {
            try {
                lazyRead(getDir(key), getFile(key));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            return startMap.get(key);
        }
    }
    
    public void getColumnTypes() throws IOException {
        checkIsNotClosed();
        File f = new File(getWorkingDirectory(), "signature.tsv");
        if (!f.exists()) {
            throw new IOException("can't get " + getName() + "'s signature: file doesn't exist");
        }
        
        Scanner sc = new Scanner(f);
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
            sb.append(" ");
        }
        String monoString = sb.toString(); 
        monoString = monoString.trim();
        String[] types = monoString.split("\\s+");
        for (String type : types) {
            columnTypes.add(TypeClass.getTypeWithName(type));
        }
        
        sc.close();
        if (columnTypes.isEmpty()) {
            throw new IOException(getName() + " has empty signature");
        } 

    }
    
    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkIsNotClosed();
        if (key == null || value == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("can't put null key or(and) value");
        }
        if (key.split("\\s+").length != 1) {
            throw new IllegalArgumentException("can't put key with spaces inside");
        }
        checkStoreable(value);
        Storeable result = get(key);
        changedKeys.get().put(key, value);
        removedKeys.get().remove(key);
        return result;
    }
    
    private void checkStoreable(Storeable s) {
        int i = 0;
        try {
            for (; i < columnTypes.size(); ++i) {
                if (s.getColumnAt(i) != null 
                         && !columnTypes.get(i).isAssignableFrom(s.getColumnAt(i).getClass())) {
                    throw new ColumnFormatException("column types are not similar");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("size is not similar");
        }
        try {
            s.getColumnAt(i);
            throw new ColumnFormatException("size is not similar");
        } catch (IndexOutOfBoundsException e) {
            // do nothing
        }
            
    }

    @Override
    public Storeable remove(String key) {
        checkIsNotClosed();
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("can't remove null key");
        }
        Storeable result = get(key);
        changedKeys.get().remove(key);
        try {
            lock.readLock().lock();
            if (getStartValue(key) != null) {
                removedKeys.get().add(key);
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }
    
    @Override 
    public String removeKey(String key) {
        checkIsNotClosed();
        try {
            return Serializer.run(this, remove(key));
        } catch (XMLStreamException e) {
            System.err.println("can't serialize to remove key: " + key);
        }
        return null;
    }
    
    @Override
    public int size() {
        checkIsNotClosed();
        int result = 0;
        try {
            lock.readLock().lock();
            result = getSize();
            for (String key : removedKeys.get()) {
                if (getStartValue(key) != null) {
                    --result;
                }
            }
            for (Entry<String, Storeable> pair : changedKeys.get().entrySet()) {
                if (getStartValue(pair.getKey()) == null) {
                    ++result;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;      
    }

    @Override
    public int commit() {
        checkIsNotClosed();
        int result = getNumberOfChanges();
        try {
            lock.writeLock().lock();
            writeSize(size());
            write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
        removedKeys.get().clear();
        changedKeys.get().clear();
        return result;
    }

    @Override
    public int rollback() {
        checkIsNotClosed();
        int result = getNumberOfChanges();
        changedKeys.get().clear();
        removedKeys.get().clear();
        return result;
    }

    private int getDir(String key) throws IOException {
        int hashcode = Math.abs(key.hashCode());
        int ndirectory = hashcode % 16;
        if (!getWorkingDirectory().exists()) {
            getWorkingDirectory().mkdir();
        }
        File dir = new File(getWorkingDirectory(), ndirectory + ".dir");
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("can't create dir");
            }
        }
        return ndirectory;
    }

    private int getFile(String key) throws IOException {
        int hashcode = Math.abs(key.hashCode());
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        File dir = new File(getWorkingDirectory(), ndirectory + ".dir");
        File file = new File(dir.getCanonicalPath(), nfile + ".dat");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("can't create file");
            }
        }
        return nfile;
    }    
    
    private File getFilePath(int dir, int file) {
        File directory = new File(getWorkingDirectory(), dir + ".dir");
        return  new File(directory, file + ".dat");
    }

    public void lazyRead(int dir, int file) throws IOException {
        File f = getFilePath(dir, file);
        if (f.isFile() && f.length() != 0) {
            try {
                readFile(f, this, startMap);
            } catch (ParseException e) {
                throw new IOException("can't deserialize: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void read() throws IOException {
        try {
            lock.writeLock().lock();
            
            startMap.clear();
            File[] dirs = getWorkingDirectory().listFiles();
            if (dirs != null) {
                if (dirs.length == 0) {
                    throw new IOException("can't read files: empty table " + getName());
                }
                for (File file : dirs) {
                    if (!file.isDirectory()) {
                        continue;
                    }
                    File[] files = file.listFiles();
                    if (files != null) {
                        if (files.length == 0) {
                            throw new IOException("can't read files: empty dir " + file.getName());
                        }
                        for (File f : files) {
                            if (f.length() == 0) {
                                throw new IOException("can't read files: empty file " + f.getName());
                            }
                            try {
                                readFile(f, this, startMap);
                            } catch (ParseException e) {
                                throw new IOException("can't deserialize");
                            }
                        }
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void readFile(File in, StoreableTableState table, AbstractMap<String, Storeable> map) 
                                                                throws IOException, ParseException {
        DataInputStream s = new DataInputStream(new FileInputStream(in));
        boolean flag = true;
        do {
            try {
                int keyLength = s.readInt();
                int valueLength = s.readInt();
                if (keyLength <= 0 || valueLength <= 0 || keyLength >= 1024 * 1024 || valueLength >= 1024 * 1024) {
                    throw new IOException("Invalid input");
                }
                byte[] tempKey = new byte[keyLength];    
                s.read(tempKey);
                String key = new String(tempKey, StandardCharsets.UTF_8);
                
                if (!in.getName().equals(getFile(key) + ".dat") 
                        || !in.getParentFile().getName().equals(getDir(key) + ".dir")) {
                    throw new IOException("wrong key " + key + " placement,expected: " + getFile(key) + " "
                                    + getDir(key) + " but: " + in.getCanonicalPath());
                }
                
                byte[] tempValue = new byte[valueLength];
                s.read(tempValue);
                String value = new String(tempValue, StandardCharsets.UTF_8);
                try {
                    map.put(key, (Deserializer.run(table, value)));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            } catch (EOFException e) {
                break;
            }
        } while (flag);
        s.close();
        
    }
    
    private int readFileForSize(File in) throws IOException {
        int size = 0;
        if (in.isFile()) {
            DataInputStream s;
            try {
                s = new DataInputStream(new FileInputStream(in));
            } catch (FileNotFoundException e1) {
                throw new RuntimeException("problem in readFileForSize()");
            }
            boolean flag = true;            
            do {
                try {
                    int keyLength = s.readInt();
                    int valueLength = s.readInt();
                    byte[] tempKey = new byte[keyLength];    
                    s.read(tempKey);
             
                    byte[] tempValue = new byte[valueLength];
                    s.read(tempValue);
                    ++size;
                } catch (EOFException e) {
                    break;
                }
            } while (flag);
            s.close();
        }
        return size;
    }
    
    public void write() throws IOException {
        checkIsNotClosed();
        Set<String> modifiedKeys = new HashSet<String>();
        modifiedKeys.addAll(removedKeys.get());
        modifiedKeys.addAll(changedKeys.get().keySet());
        ThreadLocal<HashMap<String, Storeable>> map = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            protected HashMap<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };
        
        if (getWorkingDirectory() != null) {
            for (int i = 0; i < DIR_COUNT; ++i) {
                for (int j = 0; j < FILES_PER_DIR; ++j) {
                    
                    
                    boolean toWrite = false;
                    for (String key : modifiedKeys) {
                        if (getDir(key) == i && getFile(key) == j) {
                            toWrite = true;
                            break;
                        }
                    }
                    
                    if (toWrite) {
                        map.get().clear(); 
                        File out = getFilePath(i, j);
                        try {
                            readFile(out, this, map.get());
                        } catch (ParseException e) {
                            throw new IOException(e.getMessage());
                        }
                        out.delete();
                        saveChanges();
                        if (map.get().size() > 0) {
                            out.createNewFile();
                            DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
                            Set<Entry<String, Storeable>> set = map.get().entrySet();
                            for (Entry<String, Storeable> element : set) {
                                try {
                                    if (i == getDir(element.getKey()) && j == getFile(element.getKey())) {
                                        Writer.writePair(element.getKey(), 
                                                Serializer.run(this, element.getValue()), s);
                                    }
                                } catch (XMLStreamException e) {
                                    throw new IOException(e);
                                }
                            }
                            s.close();
                        } 
                    }
                }
            }
            deleteEmptyDirs(getWorkingDirectory());
        }
    }

    private void saveChanges() {
        for (String key : removedKeys.get()) {
            startMap.remove(key);
        }
        for (Entry<String, Storeable> pair : changedKeys.get().entrySet()) {
            startMap.put(pair.getKey(), pair.getValue());
        }
    }

    private void deleteEmptyDirs(File f) {
        for (File dir : f.listFiles()) {
            if (dir.isDirectory() && dir.listFiles().length == 0) {
                dir.delete();
            }
        }
    }

    @Override
    public int getColumnsCount() {
        checkIsNotClosed();
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkIsNotClosed();
        return columnTypes.get(columnIndex);
    }
    
    public void setColumnTypes(List<Class<?>> types) {
        columnTypes = types;
    }

    @Override
    public String put(String key, String value) throws XMLStreamException, ParseException {
        checkIsNotClosed();
        Storeable storeable = put(key, Deserializer.run(this, value));
        if (storeable == null) {
            return null;
        }
        return Serializer.run(this, storeable);
    }
    
    @Override
    public Storeable get(String key) {
        checkIsNotClosed();
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("can't get null key");
        }
        if (removedKeys.get().contains(key)) {
            return null;
        }
        if (changedKeys.get().containsKey(key)) {
            return changedKeys.get().get(key);
        }
        Storeable result = null;
        try {
            lock.writeLock().lock();
            result = getStartValue(key);
        } finally {
            lock.writeLock().unlock();
        } 
        return result;
        
    } 
    
    @Override
    public String getValue(String key) {
        checkIsNotClosed();
        try {
            return Serializer.run(this, get(key));
        } catch (XMLStreamException e) {
            System.err.println("can't serialize " + key + "'s value to get it");
            return null;
        }
    }

    public HashSet<String> getRemovedKeys() {
        return removedKeys.get();
    }

    public HashMap<String, Storeable> getChangedKeys() {
        return changedKeys.get();
    }
    
    @Override
    public int getNumberOfChanges() {
        checkIsNotClosed();
        int result = 0;
        try {
            lock.readLock().lock();
            for (String key : removedKeys.get()) {
                if (getStartValue(key) != null) {
                   ++result;
                }
            }
            for (Entry<String, Storeable> pair : changedKeys.get().entrySet()) {
                if (getStartValue(pair.getKey()) == null 
                        || !getStartValue(pair.getKey()).equals(pair.getValue())) {
                    ++result;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
     
        return result;
    }

    public ReadWriteLock getLock() {
        return lock;
    }

    public void setLock(ReadWriteLock lock) {
        this.lock = lock;
    }
    
    public String toString() {
        return getClass().getSimpleName() + "[" + getWorkingDirectory().getAbsolutePath() + "]"; 
    }

    @Override
    public void close() throws Exception {
        if (!isClosed) {
            rollback();
        }
        isClosed = true;
    }
    
    private void checkIsNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("table is closed");
        }
    }
    
    public boolean isClosed() {
        return isClosed;
    }
}
