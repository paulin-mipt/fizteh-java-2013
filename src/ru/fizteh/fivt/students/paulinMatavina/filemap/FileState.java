package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;
import ru.fizteh.fivt.storage.structured.*;

public class FileState extends State {
    private HashMap<String, Storeable> initial;
    private ThreadLocal<HashMap<String, Storeable>> changes;
    public RandomAccessFile dbFile;
    public String path;
    private TableProvider provider;
    private Table table;
    private int foldNum;
    private int fileNum;
    private ReentrantReadWriteLock initialChangeLock;
    
    public FileState(String dbPath, int folder, int file, TableProvider prov, Table newTable)
                                                      throws ParseException, IOException {
        initial = new HashMap<String, Storeable>();
        foldNum = folder;
        fileNum = file;
        provider = prov;
        table = newTable;
        path = dbPath;
        initialChangeLock = new ReentrantReadWriteLock(true);
        changes = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<String, Storeable>();
            }
        };
        loadData();
    }
    
    private void fileCheck() throws IOException {
        boolean newFile = false;
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            newFile = true;
        }      
        try {
            dbFile = new RandomAccessFile(path, "rw");
            if (dbFile.length() == 0 && !newFile) {
                throw new IllegalStateException(path + " is an empty file");
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(path + " not found");
        }
        return;
    }
    
    public void assignInitial() {
        initialChangeLock.writeLock().lock();
        try {
            for (Map.Entry<String, Storeable> s : changes.get().entrySet()) {
                if (s.getValue() != null) {
                    initial.put(s.getKey(), s.getValue());
                } else {
                    initial.remove(s.getKey());
                }
            }
            changes.get().clear();
        } finally {
            initialChangeLock.writeLock().unlock();
        }
    }
    
    public void assignData() {
        changes.get().clear();
    }
    
    private String byteVectToStr(Vector<Byte> byteVect) throws IOException {
        byte[] byteKeyArr = new byte[byteVect.size()];
        for (int i = 0; i < byteVect.size(); ++i) {
            byteKeyArr[i] = byteVect.elementAt(i);
        }
        
        try {
            return new String(byteKeyArr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is unsupported by system");
        }
    }
    
    private String getKeyFromFile(int offset) throws IOException {
        dbFile.seek(offset);
        byte tempByte = dbFile.readByte();
        Vector<Byte> byteVect = new Vector<Byte>();
        while (tempByte != '\0') {  
            byteVect.add(tempByte);
            tempByte = dbFile.readByte();
        }        
        
        return byteVectToStr(byteVect);
    }
    
    private String getValueFromFile(int offset, int endOffset) throws IOException {
        if (offset < 0 || endOffset < 0) {
            throw new IOException("reading database: wrong file format");
        }
        dbFile.seek(offset);
        byte tempByte;
        Vector<Byte> byteVect = new Vector<Byte>();
        while (dbFile.getFilePointer() < dbFile.length()
                    && (int) dbFile.getFilePointer() < endOffset) { 
            tempByte = dbFile.readByte();
            byteVect.add(tempByte);
        }        
        
        return byteVectToStr(byteVect);
    }    
    
    public int loadData() throws IOException, ParseException {    
        int result = 0;  
        dbFile = null;
        try {
            File dbTempFile = new File(path);
            if (!dbTempFile.exists()) {
                return 0;
            }
            changes.set(new HashMap<String, Storeable>());
            assignInitial();
            fileCheck();  
            if (dbFile.length() == 0) {
                (new File(path)).delete();
                return 0;
            } 
            
            int position = 0;
            String key = getKeyFromFile(position);
            int startOffset = dbFile.readInt();
            int endOffset = 0;
            int firstOffset = startOffset;
            String value = "";
            String key2 = "";
            do {  
                position += key.getBytes().length + 5;
                if (position < firstOffset) {   
                    key2 = getKeyFromFile(position);
                    endOffset = dbFile.readInt();
                    value = getValueFromFile(startOffset, endOffset);
                    
                } else {
                    value = getValueFromFile(startOffset, (int) dbFile.length());
                }
                
                if (key.getBytes().length > 0) {
                    if (getFolderNum(key) != foldNum || getFileNum(key) != fileNum) {
                        throw new RuntimeException("wrong key in file");
                    }
                    result++;
                    Storeable stor = provider.deserialize(table, value);
                    initialChangeLock.writeLock().lock();
                    try {
                        initial.put(key, stor);
                    } finally {
                        initialChangeLock.writeLock().unlock();
                    }
                }
                
                key = key2;
                startOffset = endOffset;
            } while (position <= firstOffset); 
        } catch (IOException e) {
            if (e.getMessage() == null) {
                throw new IOException("wrong database file " + path, e);
            } else {
                throw e;
            }
        } finally {
            if (dbFile != null) {
                try {
                    dbFile.close();
                } catch (Throwable e) {
                    // ignore
                }
            }
        }  
        return result;        
    }
  
    public int getChangeNum() {
        int result = 0;
        initialChangeLock.readLock().lock();
        try {
            for (Map.Entry<String, Storeable> entry : changes.get().entrySet()) {
                if (entry.getValue() != null) {
                    if (initial.get(entry.getKey()) == null || !initial.get(entry.getKey()).equals(entry.getValue())) {
                        result++;
                    }          
                } else {
                    if (initial.containsKey(entry.getKey())) {
                        result++;
                    }      
                }
            }
        } finally {
            initialChangeLock.readLock().unlock();
        }
        return result;
    }  

    public void commit() throws IOException {
        dbFile = null;
        assignInitial();
        initialChangeLock.readLock().lock();
        try {
            fileCheck();
            if (size() == 0) {
                (new File(path)).delete();
                return;
            }
            int offset = 0;
            long pos = 0;
            for (Map.Entry<String, Storeable> s : initial.entrySet()) {
                if (s.getValue() != null) {
                    offset += s.getKey().getBytes("UTF-8").length + 5;
                } 
            }
            for (Map.Entry<String, Storeable> s : initial.entrySet()) {
                if (s.getValue() != null) {
                    dbFile.seek(pos);
                    dbFile.write(s.getKey().getBytes("UTF-8"));
                    dbFile.write("\0".getBytes("UTF-8"));
                    dbFile.writeInt(offset);
                    pos = (int) dbFile.getFilePointer();
                    dbFile.seek(offset);
                    byte[] value = provider.serialize(table, s.getValue()).getBytes("UTF-8");
                    dbFile.write(value);
                    offset += value.length;
                }
            }
        } finally {
            initialChangeLock.readLock().unlock();
            if (dbFile != null) {
                try {
                  dbFile.close();
                } catch (Throwable e) {
                  // ignore
                }
            }
        }
    }
    
    public int getFolderNum(String key) {
        return (Math.abs(key.getBytes()[0]) % 16);
    }
    
    public int getFileNum(String key) {
        return ((Math.abs(key.getBytes()[0]) / 16) % 16);
    }
    
    public Storeable put(String key, Storeable value) {
        Storeable change = changes.get().get(key);
        boolean exist = changes.get().containsKey(key);
        changes.get().put(key, value);
        if (exist) {
            return change;
        } else {
            initialChangeLock.readLock().lock();
            try {
                return initial.get(key);
            } finally {
                initialChangeLock.readLock().unlock();
            } 
        }   
    }
    
    public Storeable get(String key) {
        Storeable change = changes.get().get(key);
        boolean exist = changes.get().containsKey(key);
        if (exist) {
            return change;
        } else {
            initialChangeLock.readLock().lock();
            try {
                return initial.get(key);
            } finally {
                initialChangeLock.readLock().unlock();
            } 
        }
    }
    
    public Storeable remove(String key) {
        Storeable change = changes.get().get(key);
        boolean exist = changes.get().containsKey(key);
        changes.get().put(key, null);
        
        if (exist) {
            return change;
        } else {
            initialChangeLock.readLock().lock();
            try {
                return initial.get(key);
            } finally {
                initialChangeLock.readLock().unlock();
            } 
        }
    }
    
    public int size() {
        initialChangeLock.readLock().lock();
        int result = 0;
        try {
            result = initial.size();
            for (Map.Entry<String, Storeable> entry : changes.get().entrySet()) {
                if (entry.getValue() != null) {
                    if (!initial.containsKey(entry.getKey())) {
                        result++;
                    }     
                } else {
                    if (initial.containsKey(entry.getKey())) {
                        result--;
                    }
                }
            }
        } finally {
            initialChangeLock.readLock().unlock();
        }
        return result;
    }
}
