package ru.fizteh.fivt.students.msandrikova.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.msandrikova.multifilehashmap.ChangesCountingTable;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class DatabaseMap implements ChangesCountingTable {
    private File currentFile;
    private Map<String, String> originalDatabase = new HashMap<String, String>();
    private Map<String, String> updates = new HashMap<String, String>();
    private Set<String> removedFromOriginalDatabase = new HashSet<String>();
    private String name;
    private int changesCount = 0;
    
    @Override
    public int commit() { 
        int answer = this.changesCount;
        this.changesCount = 0;
        this.currentFile.delete();
        try {
            this.writeFile();
        } catch (IOException e) {
            Utils.generateAnError("Fatal error during writing", "commit", false);
        }
        if (this.size() == 0) {
            this.currentFile.delete();
        }
        return answer;
    }
    
    @Override
    public int rollback() {
        int answer = this.changesCount;
        this.changesCount = 0;
        this.removedFromOriginalDatabase.clear();
        this.updates.clear();
        return answer;
    }
    
    @Override
    public int unsavedChangesCount() {
        return changesCount;
    }
    
    @Override
    public int size() {
        return this.originalDatabase.size() + this.updates.size() 
                - this.removedFromOriginalDatabase.size();
    }
    
    public DatabaseMap(File currentDirectory, String name) {
        this.name = name;
        this.currentFile = new File(currentDirectory, name);
        if (this.currentFile.exists()) {
            if (this.currentFile.isDirectory()) {
                Utils.generateAnError("Table \"" + name 
                        + "\" can not be a directory.", "DatabaseMap", false);
            }
            try {
                this.readFile();
            } catch (IOException e) {
                Utils.generateAnError("Fatal error during reading", "use", false);
            }
            if (this.size() == 0) {
                this.delete();
            }
        }
    }
    
    public boolean checkHash(int dirNumber, int datNumber) {
        Set<String> keySet = this.originalDatabase.keySet();
        for (String key : keySet) {
            int ndirectory = Utils.getNDirectory(key);
            int nfile = Utils.getNFile(key);
            if (dirNumber != ndirectory || datNumber != nfile) {
                return false;
            }
        }
        return true;
    }
    
    private void readFile() throws IOException {
        int keyLength;
        int valueLength;
        String key;
        String value;
        DataInputStream reader = null;
        try {
            reader = new DataInputStream(new FileInputStream(this.currentFile));
            while (true) {
                try {
                    keyLength = reader.readInt();
                } catch (EOFException e) {
                    break;
                }
                    if (keyLength <= 0 || keyLength >= 1000 * 1000) {
                        Utils.generateAnError("Incorrect length of key.", "DatabaseMap", false);
                    }
                    
                    valueLength = reader.readInt();
                    if (valueLength <= 0 || valueLength >= 1000 * 1000) {
                        Utils.generateAnError("Incorrect length of value.",
                                "DatabaseMap", false);
                    }
                    
                    byte[] keyByteArray = new byte[keyLength];
                    reader.read(keyByteArray, 0, keyLength);
                    key = new String(keyByteArray);
                    
                    byte[] valueByteArray = new byte[valueLength];
                    reader.read(valueByteArray, 0, valueLength);
                    value = new String(valueByteArray);
                    
                    this.originalDatabase.put(key, value);
                
            }
        } finally {
            reader.close();
        }
    }
    
    public void writeFile() throws IOException {
        this.currentFile.createNewFile();
        for (String key : this.removedFromOriginalDatabase) {
            this.originalDatabase.remove(key);
        }
        this.originalDatabase.putAll(this.updates);
        this.updates.clear();
        this.removedFromOriginalDatabase.clear();
        DataOutputStream writer = null;
        try {
            writer = new DataOutputStream(new FileOutputStream(this.currentFile));
            Set<String> keySet = originalDatabase.keySet();
            String value;
            for (String key : keySet) {
                value = originalDatabase.get(key);
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
    
    @Override
    public String put(String key, String value) {
        String originalValue = this.originalDatabase.get(key);
        String currentValue = this.updates.get(key);
        boolean isRemoved = this.removedFromOriginalDatabase.contains(key);
        this.updates.put(key, value);
        if (originalValue == null) {
            if (currentValue == null) {
                this.changesCount++;
            }
        } else {
            if (currentValue == null) {
                if (!isRemoved) {
                    this.changesCount++;
                    this.removedFromOriginalDatabase.add(key);
                }
            }
        }
        if (originalValue != null && originalValue.equals(value)) {
            this.updates.remove(key);
            this.removedFromOriginalDatabase.remove(key);
            this.changesCount--;
        }
        if (currentValue == null && !isRemoved) {
            return originalValue;
        } else {
            return currentValue;
        }
    }
    
    @Override
    public String get(String key) {
        String answer = this.updates.get(key);
        if (answer == null && !this.removedFromOriginalDatabase.contains(key)) {
            answer = this.originalDatabase.get(key);
        }
        return answer;
    }
    
    @Override
    public String remove(String key) {
        String originalValue = this.originalDatabase.get(key);
        String currentValue = this.updates.get(key);
        boolean isRemoved = this.removedFromOriginalDatabase.contains(key);
        if (originalValue == null) {
            if (currentValue != null) {
                this.updates.remove(key);
                this.changesCount--;
            }
        } else {
            if (currentValue != null) {
                this.updates.remove(key);
            } else {
                if (!isRemoved) {
                    currentValue = originalValue;
                    this.removedFromOriginalDatabase.add(key);
                    this.changesCount++;
                }
            }
        }
        return currentValue;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void delete() {
        this.currentFile.delete();
    }

}
