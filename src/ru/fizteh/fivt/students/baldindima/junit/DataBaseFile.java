package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import ru.fizteh.fivt.storage.structured.TableProvider;


public class DataBaseFile {
    private final Map<String, String> oldMap;
    protected File dataBaseFile;
    protected String fileName;
    private TableProvider provider;
    private DataBase table;
    private int fileNumber;
    private int directoryNumber;
    static final byte OLD = 0;
    static final byte NEW = 1;
    static final byte DELETED = 2;
    static final byte MODIFIED = 3;

    private ThreadLocal<HashMap<String, String>> diffMap = new ThreadLocal<HashMap<String, String>>() {

        public HashMap<String, String> initialValue() {
            return new HashMap<String, String>();
        }
    };

    private ThreadLocal<HashSet<String>> deletedMap = new ThreadLocal<HashSet<String>>() {

        public HashSet<String> initialValue() {
            return new HashSet<String>();
        }
    };
    
    private Lock readLock;
    private Lock writeLock;


    public DataBaseFile(String fullName, int nDirectoryNumber, int nFileNumber,
                        TableProvider nProvider, DataBase nTable) throws IOException {
        fileName = fullName;
        provider = nProvider;
        table = nTable;
        dataBaseFile = new File(fileName);
        fileNumber = nFileNumber;
        directoryNumber = nDirectoryNumber;
        oldMap = new HashMap<>();
        readLock = table.readLock;
        read();
        check();

    }

    public boolean check() throws IOException {
        for (Map.Entry<String, String> curPair : oldMap.entrySet()) {
            if (!(((Math.abs(curPair.getKey().getBytes("UTF-8")[0]) % 16) == directoryNumber)
                    && ((Math.abs(curPair.getKey().getBytes("UTF-8")[0] / 16) % 16 == fileNumber)))) {
                throw new IOException("Wrong file format key[0] =  "
                        + String.valueOf(Math.abs(curPair.getKey().getBytes("UTF-8")[0]))
                        + " in file " + fileName);
            }
            try {
                provider.deserialize(table, (curPair.getValue()));
            } catch (ParseException e) {
                throw new IOException("Invalid file format! (parse exception error!)");
            }
        }
        return true;
    }


    public void read() throws IOException {
        File dataBaseDirectory = new File(dataBaseFile.getParent());
        if (dataBaseDirectory.exists() && dataBaseDirectory.list().length == 0) {
            throw new IOException("Empty dir!");
        }
        if (!dataBaseDirectory.exists() || !dataBaseFile.exists()) {
            return;
        }
        RandomAccessFile randomDataBaseFile = new RandomAccessFile(fileName, "rw");
        if (randomDataBaseFile.length() == 0) {
            randomDataBaseFile.close();
            return;
        }

        while (randomDataBaseFile.getFilePointer() < randomDataBaseFile.length() - 1) {
            int keyLength = randomDataBaseFile.readInt();
            int valueLength = randomDataBaseFile.readInt();
            if ((keyLength <= 0) || (valueLength <= 0)) {
                randomDataBaseFile.close();
                throw new IOException("wrong format");
            }

            byte[] key;
            byte[] value;
            try {
                key = new byte[keyLength];
                value = new byte[valueLength];
            } catch (OutOfMemoryError e) {
                randomDataBaseFile.close();
                throw new IOException("too large key or value");
            }
            randomDataBaseFile.read(key);
            randomDataBaseFile.read(value);
            String keyString = new String(key, "UTF-8");
            String valueString = new String(value, "UTF-8");
            oldMap.put(keyString, valueString);
        }
        randomDataBaseFile.close();
        if (oldMap.size() == 0) {
            throw new IOException("Empty file!");
        }

    }

    public int realMapSize() {
        readLock.lock();
    	try {
        	normalizeDataBaseFile();
            int result = diffMap.get().size() + oldMap.size() - deletedMap.get().size();
            for (String key : diffMap.get().keySet()) {
                if (oldMap.containsKey(key)) {
                    --result;
                }
            }
            return result;
       } finally {
    	   readLock.unlock();
       }
    	
    }

    public void write() throws IOException {
        File dataBaseDirectory = new File(dataBaseFile.getParent());
        if (realMapSize() == 0) {
            if ((dataBaseFile.exists()) && (!dataBaseFile.delete())) {
                throw new DataBaseException("Cannot delete a file!");
            }

            if (dataBaseDirectory.exists() && dataBaseDirectory.list().length <= 0) {
                if (!dataBaseDirectory.delete()) {
                    throw new DataBaseException("Cannot delete a directory");
                }
            }
        } else {
            if (!dataBaseDirectory.exists() && !dataBaseDirectory.mkdir()) {
                throw new DataBaseException("Cannot create a directory");
            }
            if (!dataBaseFile.exists()) {
                if (!dataBaseFile.createNewFile()) {
                    throw new DataBaseException("Cannot create a file " + fileName);
                }
            }
            RandomAccessFile randomDataBaseFile = new RandomAccessFile(fileName, "rw");

            randomDataBaseFile.getChannel().truncate(0);
            for (Map.Entry<String, String> curPair : oldMap.entrySet()) {

                randomDataBaseFile.writeInt(curPair.getKey().getBytes("UTF-8").length);
                randomDataBaseFile.writeInt(curPair.getValue().getBytes("UTF-8").length);
                randomDataBaseFile.write(curPair.getKey().getBytes("UTF-8"));
                randomDataBaseFile.write(curPair.getValue().getBytes("UTF-8"));


            }
            randomDataBaseFile.close();


        }
    }

    private void checkString(String str) {
        if ((str == null) || (str.trim().length() == 0)) {
            throw new IllegalArgumentException("Wrong key or value");
        }
    }

    public String put(String keyString, String valueString) {
        checkString(keyString);
        checkString(valueString);
        String result = null;
        if (diffMap.get().containsKey(keyString)) {
            result = diffMap.get().get(keyString);
        } else {
        	readLock.lock();
        	try {
        		if (oldMap.containsKey(keyString)) {
                    result = oldMap.get(keyString);
                }
        	} finally {
        		readLock.unlock();
        	}
            
        }

        if (deletedMap.get().contains(keyString)) {
            deletedMap.get().remove(keyString);
            result = null;
        }

        diffMap.get().put(keyString, valueString);
        return result;

    }


    public String get(String keyString) {
        checkString(keyString);
        if (deletedMap.get().contains(keyString)) {
            return null;
        }
        if (diffMap.get().containsKey(keyString)) {
            return diffMap.get().get(keyString);
        }

        readLock.lock();
        try {
        	if (oldMap.containsKey(keyString)) {
                return oldMap.get(keyString);
            }
        } finally {
        	readLock.unlock();
        }
        


        return null;
    }

    public String remove(String keyString) {

        checkString(keyString);
        if (deletedMap.get().contains(keyString)) {
            return null;
        }
        String result = null;
        if (diffMap.get().containsKey(keyString)) {
            result = diffMap.get().get(keyString);
            diffMap.get().remove(keyString);
            deletedMap.get().add(keyString);
            return result;
        }
        readLock.lock();
        try {
        	if (oldMap.containsKey(keyString)) {
                result = oldMap.get(keyString);
                deletedMap.get().add(keyString);
            }
        } finally {
        	readLock.unlock();
        }
        


        return result;
    }

    private void normalizeDataBaseFile() {
        Set<String> newDeleted = new HashSet<>();
        newDeleted.addAll(deletedMap.get());


        for (String key : oldMap.keySet()) {
            if (oldMap.get(key).equals(diffMap.get().get(key))) {
                diffMap.get().remove(key);
            }
            if (newDeleted.contains(key)) {
                newDeleted.remove(key);
            }
        }

        for (String key : deletedMap.get()) {
            if (diffMap.get().containsKey(key)) {
                diffMap.get().remove(key);
            }
        }

        for (String key : newDeleted) {
            deletedMap.get().remove(key);
        }
    }


    public int countCommits() {
        readLock.lock();
        try {
        	normalizeDataBaseFile();
            return diffMap.get().size() + deletedMap.get().size();
        } finally {
        	readLock.unlock();
        }
    	
    }

    public void commit() throws IOException {
        normalizeDataBaseFile();
        for (Map.Entry<String, String> node : diffMap.get().entrySet()) {
            oldMap.put(node.getKey(), node.getValue());
        }

        for (String key : deletedMap.get()) {
            oldMap.remove(key);
        }

        diffMap.get().clear();
        deletedMap.get().clear();

        write();
    }

    public void rollback() {
        diffMap.get().clear();
        deletedMap.get().clear();
    }

    public int countSize() {
        readLock.lock();
        try {
        	normalizeDataBaseFile();
            int result = diffMap.get().size() + oldMap.size() - deletedMap.get().size();
            for (String key : diffMap.get().keySet()) {
                if (oldMap.containsKey(key)) {
                    --result;
                }
            }
            return result;
        } finally {
        	readLock.unlock();
        }
    	
    }


}
