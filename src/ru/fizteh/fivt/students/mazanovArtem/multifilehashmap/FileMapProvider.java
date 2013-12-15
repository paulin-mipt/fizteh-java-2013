/*package ru.fizteh.fivt.students.mazanovArtem.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.mazanovArtem.shell.Command;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FileMapProvider implements TableProvider,Command {

    private String nameUseTable;
    private File curDir;
    FileMapStoreable dbData;

    public Map<String, Object[]> linkCommand() {
        Map<String, Object[]> commandList = new HashMap<String, Object[]>(){ {
            put("put", new Object[] {"multiPut", true, 2 });
            put("get", new Object[] {"multiGet", false, 1 });
            put("remove", new Object[] {"multiRemove", false, 1 });
            put("create", new Object[] {"multiCreate", true, 1 });
            put("drop", new Object[] {"multiDrop", false, 1 });
            put("use", new Object[] {"multiUse", false, 1 });
            put("size", new Object[] {"multiSize", false, 0 });
            put("commit", new Object[] {"multiCommit", false, 0 });
            put("rollback", new Object[] {"multiRollback", false, 0 });
        }};
        return commandList;
    }

    public String helloString() {
        return "$ ";
    }

    public void exit() {
        File tmp = curDir;
        if (nameUseTable.equals("")) {
            return;
        }
        try {
            dbData.saveTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public FileMapProvider(String pathDb) throws Exception {
        this.nameUseTable = "";
        File tmpFile = new File(pathDb);
        dbData.checkDirValidate(tmpFile);
        this.curDir = tmpFile;
        this.dbData = null;
    }

    public Table createTable(String name) throws IllegalStateException{
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is clear");
        }
        if (inCorrectDir(name)) {
            throw new RuntimeException("bad symbol in name");
        }
        Table answer = null;
        File tmpFile = new File(appendPath(name));
        if (tmpFile.exists()) {
            System.out.println(name + " exists");
        } else {
            if (!tmpFile.mkdir()) {
                throw new RuntimeException(String.format("Table %s can't be create", name));
            } else {
                System.out.println("created");
                String tmpstr = appendPath(name);
                File a = new File(tmpstr);
                try {
                    answer = new FileMapStoreable(a,name);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return answer;
    }

    public Table getTable(String name) {

    }

    public void removeTable(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name is clear");
        }
        if (inCorrectDir(name)) {
            throw new RuntimeException("bad symbol in name");
        }
        File tmpFile = new File(appendPath(name));
        if (!tmpFile.exists()) {
            throw new IllegalStateException(name + " not exists");
        } else {
            try {
                dbData.dropTable(name);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public void multiPut(String[] args) throws Exception {
        if (nameUseTable.equals("")) {
            System.out.println("no table");
        } else {
            StringBuilder arg = new StringBuilder(args[0]);
            StringBuilder tmpkey = new StringBuilder();
            String value = "";
            String key = "";
            int argLen = arg.length();
            int state = 0;
            boolean flag = false;
            for (int i = 0; i < arg.length(); ++i) {
                switch (state) {
                    case 0: if (arg.charAt(i) != ' ') {
                        flag = true;
                        state++;
                    }
                        break;
                    case 1: if (arg.charAt(i) == ' ') {
                        flag = false;
                    } else {
                        if (!flag) {
                            tmpkey.append(arg.charAt(i));
                            state++;
                            flag = true;
                        }
                    }
                        break;
                    case 2: if (arg.charAt(i) == ' ') {
                        flag = false;
                    } else {
                        if (flag) {
                            tmpkey.append(arg.charAt(i));
                            flag = true;
                        } else {
                            state++;
                            argLen--;
                        }
                    }
                        break;
                    default: argLen--;
                        break;
                }
            }
            if (state < 3) {
                throw new Exception("Few arguments");
            }
            key = tmpkey.toString();
            arg.delete(0, argLen);
            value = arg.toString();
            String res = dbData.put(key,value);
            if (res == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(res);
            }
        }
    }

    public void multiGet(String[] args) throws Exception {
        if (nameUseTable.equals("")) {
            System.out.println("no table");
        } else {
            String key = args[0];
            String res = dbData.get(key);
            if (res == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(res);
            }
        }

    }

    public void multiRemove(String[] args) throws Exception {
        if (nameUseTable.equals("")) {
            System.out.println("no table");
        } else {
            String key = args[0];
            String res = dbData.remove(key);
            if (res == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        }
    }

    public void multiCreate(String[] args) throws Exception {
        String tableName = args[0];
        Table created = createTable(tableName);
        if (created == null) {
            System.out.println(tableName + " exists");
        } else {
            System.out.println("created");
        }
    }

    public void multiDrop(String[] args) throws Exception {
        String tableName = args[0];
        if (nameUseTable.equals(tableName)) {
            nameUseTable = "";
        }
        dbData.dropTable(tableName);
        System.out.println("dropped");
    }

    public void multiUse(String[] args) throws Exception {
        String nameTable = args[0];
        File tmpFile = new File(appendPath(nameTable));
        if (!tmpFile.exists()) {
            System.out.println(nameTable + " not exists");
            return;
        }
        int changeKeys = 0;
        if (dbData != null) {
            changeKeys = dbData.sizeChanges();
        }
        if (changeKeys > 0) {
            System.out.println(String.format("%d unsaved changes", changeKeys));
        } else {
            if (!nameTable.equals(nameUseTable)) {
                dbData = (FileMapStoreable) getTable(nameTable);
                nameUseTable = nameTable;
            }
            System.out.println("using " + nameTable);
        }
    }

    public void multiCommit(String[] args) throws Exception {
        System.out.println(String.valueOf(dbData.commit()));
    }

    public void multiRollback(String[] args) throws Exception {
        System.out.println(String.valueOf(dbData.rollback()));
    }

    public void multiSize(String[] args) throws Exception {
        if (nameUseTable.equals("")) {
            System.out.println("no table");
        } else {
            System.out.println(String.valueOf(dbData.size()));
        }
    }

    private String appendPath(String path) throws RuntimeException {
        File tmp = new File(path);
        String tmpStr = "";
        if (tmp.isAbsolute()) {
            return tmp.getAbsolutePath();
        } else {
            tmp = new File(curDir.getAbsolutePath() + File.separator + path);
            try {
                tmpStr = tmp.getCanonicalPath();
            } catch (IOException e) {
                throw new RuntimeException("Can't get canonical path");
            }
            return tmpStr;
        }
    }

    public static Boolean inCorrectDir(String dir) {
        return dir.contains("/") || dir.contains(":") || dir.contains("*")
                || dir.contains("?") || dir.contains("\"") || dir.contains("\\")
                || dir.contains(">") || dir.contains("<") || dir.contains("|");
    }
} */
