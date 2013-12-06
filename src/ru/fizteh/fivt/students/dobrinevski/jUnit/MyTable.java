package ru.fizteh.fivt.students.dobrinevski.jUnit;

import java.io.File;
import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MyMultiHashMap;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MultiFileHashMapCommands;
import ru.fizteh.fivt.students.dobrinevski.shell.Command;

public class MyTable implements Table{
    public MyMultiHashMap baseHashMap;
    public String name;
    public File root;

    public MyTable(File realRoot, String tableName, MyMultiHashMap hashMap) {
        name = tableName;
        baseHashMap = hashMap;
        root = realRoot;
    }

    public String getName() {
        return name;
    }

    public String get(String key) throws IllegalArgumentException {
        if ((key == null) || (key.equals(""))) {
            throw new IllegalArgumentException("Illegal key");
        }
        if (!isValidKeyValueName(key)) {
            throw new IllegalArgumentException("Invalid name");
        }
        try {
            String[] buf = {"get", key};
            Command get = new MultiFileHashMapCommands.Get(baseHashMap, root);
            get.innerExecute(buf);
            return get.returnValue.length == 1 ? null : get.returnValue[1];
        } catch (Exception e) {
            throw new RuntimeException("Bad Table");
        }
    }

    public String put(String key, String value) {
        if (((key == null) || (key.equals(""))) || ((value == null) || (value.equals("")))) {
            throw new IllegalArgumentException("Illegal key");
        }
        if ((!isValidKeyValueName(key)) || value.contains("\n")) {
            throw new IllegalArgumentException("Invalid name");
        }
        try {
            String[] buf = {"put", key, value};
            Command put = new MultiFileHashMapCommands.Put(baseHashMap, root);
            put.innerExecute(buf);
            return put.returnValue.length == 1 ? null : put.returnValue[1];
        } catch (Exception e) {
            throw new RuntimeException("Bad Table");
        }
    }

    public String remove(String key) {
        if ((key == null) || (key.equals(""))) {
            throw new IllegalArgumentException("Illegal key");
        }
        if (!isValidKeyValueName(key)) {
            throw new IllegalArgumentException("Invalid name");
        }
        try {
            String[] buf = {"get", key};
            Command get = new MultiFileHashMapCommands.Get(baseHashMap, root);
            get.innerExecute(buf);
            new MultiFileHashMapCommands.Remove(baseHashMap, root).innerExecute(buf);
            return get.returnValue.length == 1 ? null : get.returnValue[1];
        } catch (Exception e) {
            throw new RuntimeException("Bad Table");
        }
    }

    public int size() {
        try {
            String[] buf = {"size"};
            Command size = new TransMultiFileHashMapCommands.Size(baseHashMap, root);
            size.innerExecute(buf);
            return new Integer(size.returnValue[0]);
        } catch (Exception e) {
            throw new RuntimeException("Bad Table");
        }
    }

    public int commit() {
        try {
            String[] buf = {"commit"};
            Command commit = new TransMultiFileHashMapCommands.Commit(baseHashMap, root);
            commit.innerExecute(buf);
            return new Integer(commit.returnValue[0]);
        } catch (Exception e) {
            throw new RuntimeException("Bad Table");
        }
    }

    public int rollback() {
        try {
            String[] buf = {"rollback"};
            Command rollback = new TransMultiFileHashMapCommands.RollBack(baseHashMap, root);
            rollback.innerExecute(buf);
            return new Integer(rollback.returnValue[0]);
        } catch (Exception e) {
            throw new RuntimeException("Bad Table");
        }
    }

    private static boolean isValidKeyValueName(String name) {
        return !(name.contains("\n") || name.contains("\t") || name.contains(" "));
    }
}
