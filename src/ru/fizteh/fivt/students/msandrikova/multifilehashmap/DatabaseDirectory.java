package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.msandrikova.filemap.DatabaseMap;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class DatabaseDirectory implements ChangesCountingTable {
    private String name;
    private File directoryPath;
    private Map<Integer, DatabaseMap> mapOfDatabases = new HashMap<Integer, DatabaseMap>();
    private static final int MAX_DATABASE_AMOUNT = 16;
    
    private void getDatabase(int nameNumber) {
        String name = Integer.toString(nameNumber) + ".dat";
        File databasePath = new File(this.directoryPath, name);
        if (databasePath.exists()) {
            DatabaseMap newDatabase = new DatabaseMap(this.directoryPath, name);
            if (!newDatabase.checkHash(Utils.getNameNumber(this.name), nameNumber)) {
                Utils.generateAnError("Incorrect keys in directory \"" + this.name 
                        + "\" in data base \"" + name + "\".", "use", false);
            }
            if (newDatabase.size() == 0) {
                this.mapOfDatabases.put(nameNumber, newDatabase);
            }
        }
    }

    public DatabaseDirectory(File tableDirectory, String name) {
        this.name = name;
        this.directoryPath = new File(tableDirectory, name);
        if (this.directoryPath.exists()) {
            if (!this.directoryPath.isDirectory()) {
                Utils.generateAnError("File \"" + this.name + "\" should be directory.",
                        "DatabaseDirectory", false);
            }
            for (int i = 0; i < DatabaseDirectory.MAX_DATABASE_AMOUNT; i++) {
                this.getDatabase(i);
            }
            if (this.size() == 0) {
                this.delete();
            }
        }
    }
    
    @Override
    public int size() {
        int answer = 0;
        for (DatabaseMap database : this.mapOfDatabases.values()) {
            answer += database.size();
        }
        return answer;
    }
    
    private DatabaseMap createDatabase(int nameNumber) throws IOException {
        DatabaseMap newDatabase = null;
        String name = Integer.toString(nameNumber) + ".dat";
        newDatabase = new DatabaseMap(this.directoryPath, name);
        this.mapOfDatabases.put(nameNumber, newDatabase);
        return newDatabase;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        int nfile = Utils.getNFile(key);
        String answer = null;
        DatabaseMap currentDatabase = this.mapOfDatabases.get(nfile);
        if (currentDatabase != null) {
            answer = currentDatabase.get(key);
        }
        return answer;
    }
    
    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        int nfile = Utils.getNFile(key);
        String answer = null;
        DatabaseMap currentDatabase = this.mapOfDatabases.get(nfile);
        if (currentDatabase == null) {
            try {
                currentDatabase = this.createDatabase(nfile);
            } catch (IOException e) {
                Utils.generateAnError("Can not open or use required data base.", "put", false);
            }
        }
        answer = currentDatabase.put(key, value);
        return answer;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        int nfile = Utils.getNFile(key);
        String answer = null;
        DatabaseMap currentDatabase = this.mapOfDatabases.get(nfile);
        if (currentDatabase != null) {
            answer = currentDatabase.remove(key);
        }
        return answer;
    }
    
    @Override
    public int commit() {
        int answer = 0;
        if (this.directoryPath.exists()) {
            this.delete();
        }
        this.directoryPath.mkdir();
        for (DatabaseMap database : this.mapOfDatabases.values()) {
            answer += database.commit();
        }
        if (this.size() == 0) {
            this.delete();
        }
        return answer;
    }
    
    public void delete() {
        try {
            Utils.remover(this.directoryPath, "remove", false);
        } catch (IOException e) {
            Utils.generateAnError("Fatal error during deleting", "remove", false);
        }
    }

    @Override
    public int rollback() {
        int answer = 0;
        for (DatabaseMap database : this.mapOfDatabases.values()) {
            answer += database.rollback();
        }
        return answer;
    }

    @Override
    public int unsavedChangesCount() {
        int answer = 0;
        for (DatabaseMap database : this.mapOfDatabases.values()) {
            answer += database.unsavedChangesCount();
        }
        return answer;
    }
}
