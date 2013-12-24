package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;
import ru.fizteh.fivt.students.paulinMatavina.shell.*;
import ru.fizteh.fivt.storage.structured.*;

public class FileState extends State {
    private WeakHashMap<String, Storeable> cache;
    private ThreadLocal<HashMap<String, Storeable>> changes;
    public RandomAccessFile dbFile;
    private File mainFile;
    private File tempFile;
    public String path;
    private TableProvider provider;
    private Table table;
    private int foldNum;
    private int fileNum;
    private ShellState shell;
    private ReentrantReadWriteLock cacheLock;
    private ReentrantLock offsetMapLock;
    private String parentPath;
    
    class IntPair {
        public int startIndex;
        public int endIndex;
        public IntPair(int start, int end) {
            startIndex = start; 
            endIndex = end;
        }
    }
    private HashMap<String, IntPair> key2Offset;
    public FileState(String dbPath, int folder, int file, TableProvider prov, Table newTable)
                                                      throws ParseException, IOException {
        cache = new WeakHashMap<String, Storeable>();
        cacheLock = new ReentrantReadWriteLock(true);
        offsetMapLock = new ReentrantLock(true);
        foldNum = folder;
        fileNum = file;
        provider = prov;
        table = newTable;
        path = dbPath;
        mainFile = new File(path);
        parentPath = mainFile.getParentFile().getAbsolutePath();
        tempFile = new File(parentPath + File.separator + "temp" + fileNum);
        key2Offset = new HashMap<String, FileState.IntPair>();
        
        changes = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<String, Storeable>();
            }
        };
        
        if (mainFile.exists()) {
            fileCheck();
        }
        shell = new ShellState();        
    }
    
    private void fileCheck() throws IOException {  
        if (!mainFile.exists()) {
            return;
        }
        if (mainFile.length() == 0) {
            throw new IllegalStateException(path + " is an empty file");
        }
        return;
    }
    
    public int rollback() {
        int result = getChangeNum();
        changes.get().clear();
        return result;
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
    
    private String getValueFromFile(int offset, int endOffset, RandomAccessFile dbFile) throws IOException {
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
    
    private Storeable getValue(String key) {
        Storeable result = null;
        try {
            cacheLock.readLock().lock();
            try {
                result = cache.get(key);
            } finally {
                cacheLock.readLock().unlock();
            } 
            if (result != null) {
                return result;
            } else {
                offsetMapLock.lock();
                try {
                    return loadData(key);
                } finally {
                    offsetMapLock.unlock();
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    //if requestedKey is null, then key2Offset is filling, else returns value from disk
    public Storeable loadData(String requestedKey) throws IOException, ParseException {   
        if (!mainFile.exists() || mainFile.length() == 0) {
            return null;
        }
        
        Storeable result = null;
        dbFile = null;
        try {
            dbFile = new RandomAccessFile(path, "rw");
            String key = getKeyFromFile(0);
            int startOffset = dbFile.readInt();
            int endOffset = 0;
            int firstOffset = startOffset;
            String key2 = "";
            int position = 0;
            do {  
                position = (int) dbFile.getFilePointer();
                if (position < firstOffset) {   
                    key2 = getKeyFromFile(position);
                    endOffset = dbFile.readInt();                        
                } else {
                    endOffset = (int) dbFile.length();
                }
                
                
                if (key.getBytes().length > 0) {
                    if (getFolderNum(key) != foldNum || getFileNum(key) != fileNum) {
                        throw new RuntimeException("wrong key in file");
                    }
                    if (requestedKey == null) {
                        key2Offset.put(key, new IntPair(startOffset, endOffset));
                    } else {
                        if (key.equals(requestedKey)) {
                            String strOnDisk = getValueFromFile(startOffset, endOffset, dbFile);
                            Storeable valueOnDisk = provider.deserialize(table, strOnDisk);
                            cacheLock.writeLock().lock();
                            try {
                                cache.put(key, valueOnDisk);
                            } finally {
                                cacheLock.writeLock().unlock();
                            }
                            return valueOnDisk;
                        }
                    }
                }
                
                key = key2;
                startOffset = endOffset;
            } while (position < firstOffset); 
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
        for (Map.Entry<String, Storeable> entry : changes.get().entrySet()) {
            Storeable stored = getValue(entry.getKey());
            Storeable changed = entry.getValue();
            if (changed != null) {
                if (stored == null || !stored.equals(changed)) {
                    result++;
                }          
            } else {
                if (stored != null) {
                    result++;
                }      
            }
        }
        return result;
    }  
    //return new position for key
    private int writeKeyValue(RandomAccessFile dbFile, int position, int offset, String key, String value)
                                           throws UnsupportedEncodingException, IOException {
        dbFile.seek(position);
        dbFile.write(key.getBytes("UTF-8"));
        dbFile.write("\0".getBytes("UTF-8"));
        dbFile.writeInt(offset);
        position = (int) dbFile.getFilePointer();
        dbFile.seek(offset);
        dbFile.write(value.getBytes("UTF-8"));
        return position;
    }
    
    public int commit() throws IOException, ParseException {  
        int result = getChangeNum();
        if (result == 0) {
            return 0;
        }
        if (!mainFile.exists()) {
            mainFile.createNewFile();
        }
        shell.copy(new String[] {mainFile.getAbsolutePath(), tempFile.getAbsolutePath()});
        
        RandomAccessFile tempDbFile = null;
        dbFile = null;
        int newStartOffset = 0;           
        offsetMapLock.lock();
        try {
            //calculating main offset
            loadData(null);
            dbFile = new RandomAccessFile(mainFile, "rw");
            tempDbFile = new RandomAccessFile(tempFile, "r");
            for (Iterator<Map.Entry<String, Storeable>> it = changes.get().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Storeable> s = it.next();
                if (s.getValue() == null) {
                    cacheLock.writeLock().lock();
                    try {
                        cache.remove(s.getKey());
                    } finally {
                        cacheLock.writeLock().unlock();
                    }
                    key2Offset.remove(s.getKey());
                    changes.get().remove(s.getKey());
                } else {
                    IntPair valueOffs = key2Offset.get(s.getKey());
                    if (valueOffs == null) {
                        newStartOffset += s.getKey().getBytes().length + 5;
                    } else {
                        String serial = provider.serialize(table, s.getValue());
                        String inFileValue = getValueFromFile(valueOffs.startIndex, valueOffs.endIndex, tempDbFile);
                        if (!serial.equals(inFileValue)) {
                            cacheLock.writeLock().lock();
                            try {
                                cache.put(s.getKey(), s.getValue());
                            } finally {
                                cacheLock.writeLock().unlock();
                            }
                            key2Offset.remove(s.getKey());
                            newStartOffset += s.getKey().getBytes().length + 5;
                        }
                    }
                }
            }
            for (String s : key2Offset.keySet()) {
                newStartOffset += s.getBytes().length + 5;
            }              
            
            //write it all down!
            int offset = newStartOffset; 
            int position = 0;
            for (Map.Entry<String, IntPair> s : key2Offset.entrySet()) {                
                IntPair valueOffs = s.getValue();
                String inFileValue = getValueFromFile(valueOffs.startIndex, valueOffs.endIndex, tempDbFile);
                position = writeKeyValue(dbFile, position, offset, s.getKey(), inFileValue);
                offset += inFileValue.getBytes("UTF-8").length;
            }             
                       
            for (Map.Entry<String, Storeable> s : changes.get().entrySet()) {
                String value = provider.serialize(table, s.getValue());
                position = writeKeyValue(dbFile, position, offset, s.getKey(), value);
                offset += value.getBytes("UTF-8").length;
            } 
            
            if (dbFile.length() == 0) {
                mainFile.delete();
            }
            tempFile.delete();
            changes.get().clear();
            key2Offset.clear();
        } finally {
            if (dbFile != null) {
                try {
                    dbFile.close();
                  } catch (Throwable e) {
                    // ignore
                  }
            }
            if (tempFile != null) {
                try {
                    tempDbFile.close();
                  } catch (Throwable e) {
                    // ignore
                  }
            }
            offsetMapLock.unlock();
        }  
        return result;
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
            Storeable stored = getValue(key);
            return stored;
        }   
    }
    
    public Storeable get(String key) {
        Storeable change = changes.get().get(key);
        boolean exist = changes.get().containsKey(key);
        if (exist) {
            return change;
        } else {
            Storeable stored = getValue(key);
            return stored;
        }   
    }
    
    public Storeable remove(String key) {
        Storeable change = changes.get().get(key); 
        boolean exist = changes.get().containsKey(key);        
        changes.get().put(key, null);
        
        if (exist) {
            return change;
        } else {
            Storeable stored = getValue(key);
            return stored;
        }  
    }
    
    public int getDeltaSize() {
        int result = 0;
        for (Map.Entry<String, Storeable> entry : changes.get().entrySet()) {
            Storeable stored = getValue(entry.getKey());
            Storeable changed = entry.getValue();
            if (changed != null) {
                if (stored == null) {
                    result++;
                }          
            } else {
                if (stored != null) {
                    result--;
                }      
            }
        }
        return result;
    }
}
