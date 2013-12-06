package ru.fizteh.fivt.students.elenav.storeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.utils.Writer;

public class StoreableTableState extends FilesystemState implements Table {

    private static final int DIR_COUNT = 16;
    private static final int FILES_PER_DIR = 16;
    
    private List<Class<?>> columnTypes = new ArrayList<>();
    private volatile HashMap<String, Storeable> startMap = new HashMap<>();
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
    
    public void getColumnTypes() throws IOException {
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
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("can't remove null key");
        }
        Storeable result = get(key);
        changedKeys.get().remove(key);
        try {
            lock.readLock().lock();
            if (startMap.get(key) != null) {
                removedKeys.get().add(key);
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }
    
    @Override 
    public String removeKey(String key) {
        try {
            return Serializer.run(this, remove(key));
        } catch (XMLStreamException e) {
            System.err.println("can't serialize to remove key: " + key);
        }
        return null;
    }
    
    @Override
    public int size() {
        int result = 0;
        try {
            lock.readLock().lock();
            result = startMap.size();
            for (String key : removedKeys.get()) {
                if (startMap.get(key) != null) {
                    --result;
                }
            }
            for (Entry<String, Storeable> pair : changedKeys.get().entrySet()) {
                if (startMap.get(pair.getKey()) == null) {
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
        int result = 0;
        try {
            lock.writeLock().lock();
            result = getNumberOfChanges();
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
                                readFile(f, this);
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
    
    public void readFile(File in, StoreableTableState table) throws IOException, ParseException {
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
                    table.startMap.put(key, Deserializer.run(table, value));
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            } catch (EOFException e) {
                break;
            }
        } while (flag);
        s.close();
        
    }
    
    public void write() throws IOException {
        if (getWorkingDirectory() != null) {
            saveChanges();
            for (int i = 0; i < DIR_COUNT; ++i) {
                for (int j = 0; j < FILES_PER_DIR; ++j) {
                    Map<String, Storeable> toWriteInCurFile = new HashMap<>();
            
                    for (String key : startMap.keySet()) {
                        if (getDir(key) == i && getFile(key) == j) {
                            toWriteInCurFile.put(key, startMap.get(key));
                        }
                    }
                    
                    File dir = new File(getWorkingDirectory(), i + ".dir"); 
                    File out = new File(dir, j + ".dat");
                    out.delete();
                    if (toWriteInCurFile.size() > 0) {
                        out.createNewFile();
                        DataOutputStream s = new DataOutputStream(new FileOutputStream(out));
                        Set<Entry<String, Storeable>> set = toWriteInCurFile.entrySet();
                        for (Entry<String, Storeable> element : set) {
                            try {
                                Writer.writePair(element.getKey(), 
                                        Serializer.run(this, element.getValue()), s);
                            } catch (XMLStreamException e) {
                                throw new IOException(e);
                            }
                        }
                        s.close();
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
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypes.get(columnIndex);
    }
    
    public void setColumnTypes(List<Class<?>> types) {
        columnTypes = types;
    }

    @Override
    public String put(String key, String value) throws XMLStreamException, ParseException {
        Storeable storeable = put(key, Deserializer.run(this, value));
        if (storeable == null) {
            return null;
        }
        return Serializer.run(this, storeable);
    }
    
    @Override
    public Storeable get(String key) {
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
            lock.readLock().lock();
            result = startMap.get(key);
        } finally {
            lock.readLock().unlock();
        } 
        return result;
        
    } 
    
    @Override
    public String getValue(String key) {
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
        int result = 0;
        try {
            lock.readLock().lock();
            for (String key : removedKeys.get()) {
                if (startMap.get(key) != null) {
                    ++result;
                }
            }
            for (Entry<String, Storeable> pair : changedKeys.get().entrySet()) {
                if (startMap.get(pair.getKey()) == null 
                        || !startMap.get(pair.getKey()).equals(pair.getValue())) {
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
}
