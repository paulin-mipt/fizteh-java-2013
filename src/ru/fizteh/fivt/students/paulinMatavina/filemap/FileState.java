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
import java.util.WeakHashMap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;
import ru.fizteh.fivt.storage.structured.*;

public class FileState extends State {
    private WeakHashMap<String, Storeable> cache;
    private ThreadLocal<HashMap<String, Storeable>> changes;
    public RandomAccessFile dbFile;
    public String path;
    private TableProvider provider;
    private Table table;
    private int foldNum;
    private int fileNum;
    
    public FileState(String dbPath, int folder, int file, TableProvider prov, Table newTable)
                                                      throws ParseException, IOException {
        cache = new WeakHashMap<String, Storeable>();
        foldNum = folder;
        fileNum = file;
        provider = prov;
        table = newTable;
        path = dbPath;
        changes = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<String, Storeable>();
            }
        };
        
        if (new File(path).exists()) {
            fileCheck();
        }
    }
    
    private void fileCheck() throws IOException {  
        if (!new File(path).exists()) {
            return;
        }
        
        try {
            dbFile = new RandomAccessFile(path, "rw");
            if (dbFile.length() == 0) {
                throw new IllegalStateException(path + " is an empty file");
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(path + " not found");
        }
        return;
    }
    
    private HashMap<String, Storeable> dataToWrite() throws IOException {
        HashMap<String, Storeable> currentInFile = new HashMap<String, Storeable>();
        loadData(null, currentInFile);
        
        for (Map.Entry<String, Storeable> entry : changes.get().entrySet()) {
            Storeable changed = entry.getValue();
            if (changed != null) {
                currentInFile.put(entry.getKey(), changed);         
            } else {
                currentInFile.remove(entry.getKey());
            }
        }
        
        changes.get().clear();
        return currentInFile;
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
    
    private Storeable getValue(String key) {
        Storeable result = null;
        try {
            result = cache.get(key);
            if (result != null) {
                return result;
            }
            
            result = loadData(key, cache);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }
    
    public Storeable loadData(String requestedKey, Map<String, Storeable> map) throws IOException {    
        Storeable result = null;  
        dbFile = null;
        try {
            map.clear();
            File dbTempFile = new File(path);
            if (!dbTempFile.exists()) {
                return null;
            }
            fileCheck();
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
                    
                    Storeable stor = provider.deserialize(table, value);
                    map.put(key, stor);
                    if (key.equals(requestedKey)) {
                        return stor;
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
        } catch (ParseException e) {
            throw new IOException(e.getMessage(), e);
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

    public int commit() throws IOException {
        dbFile = null;
        int result = getChangeNum();
        if (result == 0) {
            return 0;
        }
        try {
            HashMap<String, Storeable> toWrite = dataToWrite();
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
                try {
                    dbFile = new RandomAccessFile(path, "rw");
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException(path + " not found");
                }
            }
            int offset = 0;
            long pos = 0;
            for (Map.Entry<String, Storeable> s : toWrite.entrySet()) {
                if (s.getValue() != null) {
                    offset += s.getKey().getBytes("UTF-8").length + 5;
                } 
            }
            for (Map.Entry<String, Storeable> s : toWrite.entrySet()) {
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
            if (dbFile.length() == 0) {
                (new File(path)).delete();
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
