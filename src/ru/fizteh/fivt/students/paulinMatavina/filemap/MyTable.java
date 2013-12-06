package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.paulinMatavina.shell.ShellState;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;
import java.text.ParseException;

public class MyTable extends State implements Table, AutoCloseable {
    public MyTableProvider provider;
    static final int FOLDER_NUM = 16;
    static final int FILE_IN_FOLD_NUM = 16;
    private String tableName;
    FileState[][] data;
    public ShellState shell;
    private String rootPath;
    private volatile boolean isDropped;
    private volatile boolean isClosed;
    
    private List<Class<?>> objList;
    private final String signatureName = "signature.tsv";
    public HashMap<Class<?>, String> possibleTypes;
    private ReentrantReadWriteLock diskOperationLock;
    private ReentrantReadWriteLock sizeLock;
    
    private void init(String dbName) throws IOException, ParseException {   
        diskOperationLock = new ReentrantReadWriteLock(true);
        sizeLock = new ReentrantReadWriteLock(true);
        isDropped = false;
        isClosed = false;
        data = new FileState[FOLDER_NUM][FILE_IN_FOLD_NUM];
        shell = new ShellState();
        currentDir = new File(rootPath);
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            throw new IOException("wrong root directory " + rootPath);
        }
        tableName = dbName;
        
        shell.cd(rootPath);
        shell.cd(dbName);
        
        loadData();
        writeSize();
    }
    
    public MyTable(String property, String dbName, MyTableProvider prov, List<Class<?>> columnTypes) 
                                                throws ParseException, IOException {
        validate(property);
        validate(dbName);
        checkList(columnTypes);
        if (property == null || property.trim().isEmpty()) {
            throw new IOException("no root directory");
        }
        
        tableName = dbName;
        provider = prov;
        rootPath = property;  
        objList = columnTypes;
        init(dbName);
        
        writeObjList(objList, signatureName);
    }
    
    private void checkList(List<Class<?>> columnTypes) {
        possibleTypes = new HashMap<Class<?>, String>();
        possibleTypes.put(String.class, "String");
        possibleTypes.put(Integer.class, "int");
        possibleTypes.put(Boolean.class, "boolean");
        possibleTypes.put(Float.class, "float");
        possibleTypes.put(Double.class, "double");
        possibleTypes.put(Byte.class, "byte");
        possibleTypes.put(Long.class, "long");
        if (columnTypes == null) {
            throw new IllegalArgumentException("no list passed");
        }
        for (int i = 0; i < columnTypes.size(); i++) {
            if (columnTypes.get(i) == null
                    || !possibleTypes.keySet().contains(columnTypes.get(i))) {
                throw new DbWrongTypeException("incorrect type name " + columnTypes.get(i));
           }
        }
    }
    
    public MyTable(String property, String dbName, MyTableProvider prov) 
            throws ParseException, IOException {
        validate(property);
        validate(dbName);
        tableName = dbName;
        if (property == null || property.trim().isEmpty()) {
            throw new IllegalArgumentException("no root directory");
        }
        provider = prov;
        rootPath = property; 
        shell = new ShellState();
        shell.cd(rootPath);
        shell.cd(dbName);
        getObjList(signatureName);
        init(dbName);
    }
    
    private void checkDbDir(String path) {
        File f = new File(path);        
        if (f.exists()) {
            if (!f.isDirectory()) {
                throw new IllegalStateException(path + " is not a directory");
            }          
            for (String file : f.list()) {
                if (file.equals(signatureName) || file.equals("size.tsv")) {
                    continue;
                }              
                if (!file.matches("([0-9]|1[0-5])\\.dir")) {
                    throw new IllegalStateException(file + " has a wrong name");
                }
            }
        }
    }
    
    private void checkFolder(String path) {
        File f = new File(path);        
        if (f.exists()) {
            if (!f.isDirectory()) {
                throw new IllegalStateException(path + " is not a directory");
            }  
            if (f.list().length == 0) {
                throw new IllegalStateException(path + " is an empty directory");
            }
            for (String file : f.list()) {               
                if (!file.matches("([0-9]|1[0-5])\\.dat")) {
                    throw new IllegalStateException(file + " has a wrong name");
                }
            }
        }
    }
    
    private void loadData() throws IOException, ParseException {
        checkDbDir(shell.currentDir.getAbsolutePath());
        
        data = new FileState[FOLDER_NUM][FILE_IN_FOLD_NUM];
        for (int i = 0; i < FOLDER_NUM; i++) {
            String fold = Integer.toString(i) + ".dir";
            if (!fileExist(fold)) {
                for (int j = 0; j < FILE_IN_FOLD_NUM; j++) {
                    String file = Integer.toString(j) + ".dat";
                    String filePath = shell.makeNewSource(fold, file);
                    data[i][j] = new FileState(filePath, i, j, provider, this);
                }
                continue;
            }
            checkFolder(shell.makeNewSource(fold));
            for (int j = 0; j < FILE_IN_FOLD_NUM; j++) {
                String file = Integer.toString(j) + ".dat";
                String filePath = shell.makeNewSource(fold, file);
                data[i][j] = new FileState(filePath, i, j, provider, this);
            }
        }
    }
    
    public void dropped() {
        isDropped = true;
    }
    
    private void checkCurrentTableState() {
        if (isClosed) {
            throw new IllegalStateException("table was closed");
        }  
        if (isDropped) {
            throw new IllegalStateException("table was removed");
        }  
    }
    
    @Override   
    public int commit() throws IOException {
        checkCurrentTableState();
        
        int chNum = 0;
        
        diskOperationLock.writeLock().lock();
        try {
            chNum = changesNum();
            writeSize();
            for (int i = 0; i < FOLDER_NUM; i++) {
                String fold = Integer.toString(i) + ".dir";
                if (!fileExist(fold)) {
                    shell.mkdir(new String[] {fold});
                }
                for (int j = 0; j < FILE_IN_FOLD_NUM; j++) {
                    data[i][j].commit();
                }
               
                File folderFile = new File(shell.makeNewSource(fold));
                if (folderFile.exists() && folderFile.listFiles().length == 0) {
                    String[] arg = {shell.makeNewSource(fold)};
                    shell.rm(arg);
                }
            }
        } finally {
            diskOperationLock.writeLock().unlock();
        }
        return chNum;
    }
    
    private int getFolderNum(String key) {
        byte[] bytes = key.getBytes();
        return (Math.abs(bytes[0]) % 16);
    }
    
    private int getFileNum(String key) {
        byte[] bytes = key.getBytes();
        return (Math.abs(bytes[0]) / 16 % 16);
    }
    
    private void checkStoreable(Storeable value) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("no storeable was passed");
        }
        for (int i = 0; i < objList.size(); i++) {
            try {
                if (value.getColumnAt(i) != null && value.getColumnAt(i).getClass() != objList.get(i)) {
                    throw new ColumnFormatException("expected " + objList.get(i).toString()
                            + ", " + value.getColumnAt(i).getClass() + " passed");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException(e.getMessage(), e);
            }
        }
        
        try {
            value.getColumnAt(objList.size());
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        throw new ColumnFormatException("more tokens than expected");
    }
    
    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException { 
        validate(key);
        checkCurrentTableState();
        checkStoreable(value);
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        Storeable result;
        
        result = data[folder][file].put(key, value);
        return result;  
    }
    
    @Override
    public Storeable get(String key) {
        validate(key);
        checkCurrentTableState();
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        
        return data[folder][file].get(key);  
    }   
    
    @Override
    public Storeable remove(String key) {
        validate(key);
        checkCurrentTableState();
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        
        Storeable result = data[folder][file].remove(key);
        return result;  
    }
    
    @Override
    public int size() {
        checkCurrentTableState();
             
        int added = 0;
        int deleted = 0;
        sizeLock.readLock().lock();
        try {
            for (int i = 0; i < FOLDER_NUM; i++) {
                for (int j = 0; j < FILE_IN_FOLD_NUM; j++) {
                     added += data[i][j].added();
                     deleted += data[i][j].deleted();
                }       
            }
            
            int result = readSize() + added - deleted;
            if (result < 0) {
                throw new IllegalStateException("size.tsv: broken file");
            }
            return result;  
        } finally {
            sizeLock.readLock().unlock();
        }
    }
    
    private int readSize() {
        int result = 0;
        File sizeFile = new File(shell.makeNewSource("size.tsv"));
        if (!sizeFile.exists()) {
            try {
                sizeFile.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException("error when creating size.tsv", e);
            }
            return 0;
        }
        
        try (FileInputStream in = new FileInputStream(sizeFile);
                DataInputStream intReader = new DataInputStream(in)) {
            result = intReader.readInt();
        } catch (Exception e) { 
            throw new RuntimeException("error reading size.tsv", e);
        } 
        return result;
    }
    
    private void writeSize() {
        File sizeFile = new File(shell.makeNewSource("size.tsv"));
        int result = -1;
        if (!sizeFile.exists()) {
            try {
                sizeFile.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException("error when creating size.tsv", e);
            }
            result = 0;
        }
        
        if (result < 0) {
            result = size();
        }
        
        try (FileOutputStream out = new FileOutputStream(sizeFile);
                DataOutputStream writer = new DataOutputStream(out)) {
            writer.writeInt(result);
        } catch (Exception e) { 
            throw new RuntimeException("error writing size.tsv", e);
        } 
    }
    
    @Override
    public int rollback() {
        checkCurrentTableState();
        
        int chNum = 0;
        
        diskOperationLock.readLock().lock();
        try {
            chNum = changesNum();
            for (int i = 0; i < FOLDER_NUM; i++) {
                for (int j = 0; j < FILE_IN_FOLD_NUM; j++) {
                    data[i][j].assignData();
                }
            }
        } finally {
            diskOperationLock.readLock().unlock();
        }
        return chNum;
    }
    
    @Override
    public String getName() {
        checkCurrentTableState();
        return tableName;
    }
    
    private void validate(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("empty parameter");
        }
        if (key.matches(".*\\s.*")) {
            throw new IllegalArgumentException("whitespace symbols in key");
        }
    }
    
    public int changesNum() {
        int result = 0;
        
        for (int i = 0; i < FOLDER_NUM; i++) {
            for (int j = 0; j < FILE_IN_FOLD_NUM; j++) {
                result += data[i][j].getChangeNum();
            }
        } 
        return result;
    }

    @Override
    public int getColumnsCount() {
        checkCurrentTableState();
        
        return objList.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkCurrentTableState();
        
        if (columnIndex < 0 || columnIndex >= objList.size()) {
            throw new IndexOutOfBoundsException("wrong column index passed: " + columnIndex);
        } else {
            return objList.get(columnIndex);
        }
    }
    
    private void getObjList(String name) {
        File signature = new File(shell.makeNewSource(name));
        Scanner reader = null;
       
        try {
            reader = new Scanner(signature);    
            String signLine;
            signLine = reader.nextLine();
            objList = provider.parseSignature(new StringTokenizer(signLine));
        } catch (Exception e) {
            throw new RuntimeException("no correct signature file", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable e) {
                //do nothing
            }
        }
    }
    
    private void writeObjList(List<Class<?>> list, String name) throws IOException {
        FileWriter writer = null;
        
        try {
            writer = new FileWriter(new File(shell.makeNewSource(signatureName)));
            for (int i = 0; i < objList.size() - 1; i++) {
                writer.write(possibleTypes.get(objList.get(i)) + " ");
            }
            writer.write(possibleTypes.get(objList.get(objList.size() - 1)));
        } catch (IOException e) { 
            throw new IOException("error writing " + signatureName, e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Throwable e) {
                //do nothing
            }
        }
    }
    
    public boolean fileExist(String name) {
        return new File(shell.makeNewSource(name)).exists();
    }
    
    @Override
    public String toString() {        
        String result = getClass().getSimpleName() + "[" + shell.currentDir.getAbsolutePath() + "]";
        return result;
    }

    @Override
    public void close() throws Exception {
        if (!isClosed) {
            rollback();  
            isClosed = true;
        }      
    }
    
    public boolean wasClosed() {
        return isClosed;
    }
}
