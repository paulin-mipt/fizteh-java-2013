package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class MyTable implements ChangesCountingTable {
    private String name;
    private File tablePath;
    private Map<Integer, DatabaseDirectory> mapOfDirectories = new HashMap<Integer, DatabaseDirectory>();
    private static final int MAX_DIRECTORIES_AMOUNT = 16;
    private static final int MAX_TABLE_SIZE = 1000 * 1000 * 100;
    
    private void getDirectory(int nameNumber) {
        String name = Integer.toString(nameNumber) + ".dir";
        File dirPath = new File(this.tablePath, name);
        if (dirPath.exists()) {
            if (!dirPath.isDirectory()) {
                Utils.generateAnError("File \"" + name + "\"should be directory in table \"" 
                        + this.name + ".", "create", false);
            }
            DatabaseDirectory newDirectory = new DatabaseDirectory(this.tablePath, name);
            if (newDirectory.size() != 0) {
                this.mapOfDirectories.put(nameNumber, newDirectory);
            }
        }
    }
    
    public MyTable(File parentDirectory, String name) {
        this.name = name;
        this.tablePath = new File(parentDirectory, name);
        if (!tablePath.exists()) {
            this.tablePath.mkdir();
        } else {
            if (!this.tablePath.isDirectory()) {
                Utils.generateAnError("Table with name \"" + this.name 
                        + "\"should be directory.", "create", false);
            }
            for (int i = 0; i < MyTable.MAX_DIRECTORIES_AMOUNT; i++) {
                this.getDirectory(i);
            }
        }
        if (this.size() > MyTable.MAX_TABLE_SIZE) {
            Utils.generateAnError("Table \"" + this.name + "\" is overly big.", "use", false);
        }
    }
    
    private DatabaseDirectory createDirectory(int nameNumber) throws IOException, FileNotFoundException {
        DatabaseDirectory newDirectory = null;
        String name = Integer.toString(nameNumber) + ".dir";
        newDirectory = new DatabaseDirectory(this.tablePath, name);
        this.mapOfDirectories.put(nameNumber, newDirectory);
        return newDirectory;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (Utils.isEmpty(key)) {
            throw new IllegalArgumentException("Key can not be null");
        }
        int ndirectory = Utils.getNDirectory(key);
        String answer = null;
        DatabaseDirectory currentDirectory = this.mapOfDirectories.get(ndirectory);
        if (currentDirectory != null) {
            answer = currentDirectory.get(key);
        }
        return answer;
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        if (Utils.isEmpty(key) || Utils.isEmpty(value)) {
            throw new IllegalArgumentException("Key and name can not be null or newline");
        }
        int ndirectory = Utils.getNDirectory(key);
        String answer = null;
        DatabaseDirectory currentDirectory = this.mapOfDirectories.get(ndirectory);
        if (currentDirectory == null) {
            try {
                currentDirectory = this.createDirectory(ndirectory);
            } catch (IOException e) {
                Utils.generateAnError("Can not open or use required data base directory.",
                        "put", false);
            }
        }
        answer = currentDirectory.put(key, value);
        if (answer == null) {
            if (this.size() > MyTable.MAX_TABLE_SIZE) {
                Utils.generateAnError("Table \"" + this.name + "\" is overly big.", "use", false);
            }
        }
        return answer;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (Utils.isEmpty(key)) {
            throw new IllegalArgumentException();
        }
        int ndirectory = Utils.getNDirectory(key);
        String answer = null;
        DatabaseDirectory currentDirectory = this.mapOfDirectories.get(ndirectory);
        if (currentDirectory != null) {
            answer = currentDirectory.remove(key);
        }
        return answer;
    }
    

    @Override
    public int size() {
        int answer = 0;
        for (DatabaseDirectory databaseDirectory : this.mapOfDirectories.values()) {
            answer += databaseDirectory.size();
        }
        return answer;
    }

    @Override
    public int commit() {
        int answer = 0;
        for (DatabaseDirectory databaseDirectory : this.mapOfDirectories.values()) {
            answer += databaseDirectory.commit();
        }
        return answer;
    }

    @Override
    public int rollback() {
        int answer = 0;
        for (DatabaseDirectory databaseDirectory : this.mapOfDirectories.values()) {
            answer += databaseDirectory.rollback();
        }
        return answer;
    }

    @Override
    public int unsavedChangesCount() {
        int answer = 0;
        for (DatabaseDirectory databaseDirectory : this.mapOfDirectories.values()) {
            answer += databaseDirectory.unsavedChangesCount();
        }
        return answer;
    }

}
