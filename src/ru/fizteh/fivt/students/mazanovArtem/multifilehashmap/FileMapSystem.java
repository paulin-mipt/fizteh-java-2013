package ru.fizteh.fivt.students.mazanovArtem.multifilehashmap;

import ru.fizteh.fivt.students.mazanovArtem.shell.Command;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;

public class FileMapSystem implements Command {
    private File curDir;
    HashMap<String, String>[][] map = new HashMap[16][16];
    int maxLength;
    String nameUseTable = "";


    public File getFile() {
        return curDir;
    }

    public FileMapSystem(File dir) {
        if (dir == null) {
            throw new IllegalArgumentException("null direction");
        }
        curDir = dir;
        maxLength = 1 << 24;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                map[i][j] = new HashMap<>();
            }
        }
    }

    public void exit() {
        File tmp = curDir;
        if (nameUseTable.equals("")) {
            return;
        }
        try {
            saveTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String helloString() throws IOException {
        //return getFile().getCanonicalPath() + "$ ";
        return "$ ";
    }

    @Override
    public Map<String, Object[]> linkCommand() {
        Map<String, Object[]> commandList = new HashMap<String, Object[]>(){ {
            put("put", new Object[] {"put", true, 2 });
            put("get", new Object[] {"get", false, 1 });
            put("remove", new Object[] {"remove", false, 1});
            put("create", new Object[] {"create", false, 1 });
            put("drop", new Object[] {"drop", false, 1 });
            put("use", new Object[] {"use", false, 1 });
        }};
        return commandList;
    }

    public void create(String[] args) throws Exception {
        checkAmountArgs(args.length, 1);
        File tmpFile = new File(appendPath(args[0]));
        if (tmpFile.exists()) {
            System.out.println("tablename exist");
        } else {
            if (!tmpFile.mkdir()) {
                throw new Exception(String.format("Table %s can't be create", args[0]));
            } else {
                System.out.println("created");
            }
        }
    }

    public void drop(String[] args) throws Exception {
        checkAmountArgs(args.length, 1);
        File tmpFile = new File(appendPath(args[0]));
        if (nameUseTable.equals(args[0])) {
            cleanHashMap();
            nameUseTable = "";
        }
        if (tmpFile.exists()) {
            dropTable(args[0]);
            System.out.println("dropped");
        } else {
            System.out.println(args[0] + " not exists");
        }
    }

    public void use(String[] args) throws Exception {
        checkAmountArgs(args.length, 1);
        File tmpFile = new File(appendPath(args[0]));
        if (tmpFile.exists()) {
            if (!nameUseTable.equals(args[0])) {
                if (!nameUseTable.equals("")) {
                    saveTable();
                }
                nameUseTable = args[0];
                loadTable();
            }
            System.out.println("using " + nameUseTable);
        } else {
            System.out.println(args[0] + " not exists");
        }
    }

    public void put(String[] args) throws Exception {
        if (nameUseTable.equals("")) {
            throw new Exception("no table");
        }
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

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        int firstByte = Math.abs(keyBytes[0]);
        int nDirectory = firstByte % 16;
        int nFile = firstByte / 16 % 16;

        String tmp = map[nDirectory][nFile].get(key);
        if (tmp != null) {
            System.out.println("overwrite");
            System.out.println(tmp);
        } else {
            System.out.println("new");
        }
        map[nDirectory][nFile].put(key, value);
    }

    public void get(String[] args) throws Exception {
        if (nameUseTable.equals("")) {
            throw new Exception("no table");
        }
        checkAmountArgs(args.length, 1);
        String key = args[0];
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        int firstByte = Math.abs(keyBytes[0]);
        int nDirectory = firstByte % 16;
        int nFile = firstByte / 16 % 16;
        String tmp = map[nDirectory][nFile].get(key);
        if (tmp == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(tmp);
        }
    }

    public void remove(String[] args) throws Exception {
        if (nameUseTable.equals("")) {
            throw new Exception("no table");
        }
        checkAmountArgs(args.length, 1);
        String key = args[0];
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        int firstByte = Math.abs(keyBytes[0]);
        int nDirectory = firstByte % 16;
        int nFile = firstByte / 16 % 16;
        String tmp = map[nDirectory][nFile].get(key);
        if (tmp == null) {
            System.out.println("not found");
        } else {
            map[nDirectory][nFile].remove(key);
            System.out.println("removed");
        }
    }

    private void saveTable() throws Exception {
        File tmpFile = new File(appendPath(nameUseTable));
        if (!tmpFile.exists() || !tmpFile.isDirectory()) {
            if (!tmpFile.mkdir()) {
                throw new Exception("Can't create directory");
            }
        }
        String tmp = nameUseTable;
        nameUseTable = "";
        dropTable(tmp);
        nameUseTable = tmp;
        if (!tmpFile.exists()) {
            if (!tmpFile.mkdir()) {
                throw new Exception("Can't create directory and save table");
            }
        }
        curDir = tmpFile;
        for (int i = 0; i < 16; ++i) {
            tmpFile = new File(appendPath(String.valueOf(i) + ".dir"));
            for (int j = 0; j < 16; ++j) {
                if (!map[i][j].isEmpty()) {
                    if (!tmpFile.exists()) {
                        if (!tmpFile.mkdir()) {
                            throw new Exception("Can't create directory and save table");
                        }
                        break;
                    }
                }
            }
            if (!tmpFile.exists()) {
                continue;
            }
            curDir = tmpFile;
            for (int j = 0; j < 16; ++j) {
                if (map[i][j].isEmpty()) {
                    continue;
                }
                tmpFile = new File(appendPath(String.valueOf(j) + ".dat"));
                if (!tmpFile.createNewFile()) {
                    throw new Exception("Can't create file and save table");
                }
            }
            curDir = new File(appendPath(".."));
        }
        for (int i = 0; i < 16; ++i) {
            tmpFile = new File(appendPath(String.valueOf(i) + ".dir"));
            curDir = tmpFile;
            for (int j = 0; j < 16; ++j) {
                tmpFile = new File(appendPath(String.valueOf(j) + ".dat"));
                if (map[i][j].isEmpty()) {
                    continue;
                }
                //System.out.println(tmpFile + " saveTable");
                try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(tmpFile))) {
                    for (String keys : map[i][j].keySet()) {
                        byte[] key = keys.getBytes(StandardCharsets.UTF_8);
                        byte[] value = map[i][j].get(keys).getBytes(StandardCharsets.UTF_8);
                        outputStream.write(ByteBuffer.allocate(4).putInt(key.length).array());
                        outputStream.write(ByteBuffer.allocate(4).putInt(value.length).array());
                        outputStream.write(key);
                        outputStream.write(value);
                    }
                } catch (FileNotFoundException e) {
                    throw new Exception("File not found");
                }
            }
            curDir = new File(appendPath(".."));
        }
        curDir = new File(appendPath(".."));
    }

    private void loadTable() throws Exception {
        cleanHashMap();
        if (!curDir.getParentFile().exists() || !curDir.getParentFile().isDirectory()) {
            throw new Exception("directory doesn't exist");
        }
        File tmpFile = new File(appendPath(nameUseTable));
        curDir = tmpFile;
        checkDirValidate();
        for (int i = 0; i < 16; ++i) {
            tmpFile = new File(appendPath(String.valueOf(i) + ".dir"));
            if (!tmpFile.exists()) {
                continue;
            }
            curDir = tmpFile;
            for (int j = 0; j < 16; ++j) {
                tmpFile = new File(appendPath(String.valueOf(j) + ".dat"));
                if (!tmpFile.exists()) {
                    continue;
                }
                try (DataInputStream inputStream = new DataInputStream(new FileInputStream(tmpFile))) {
                    byte[] buffer;
                    ByteBuffer cast;
                    int countBytes;
                    int keyLength;
                    int valueLength;
                    String key;
                    String value;

                    while (true) {
                        buffer = new byte[4];
                        countBytes = readBytes(inputStream, 4, buffer);
                        if (countBytes == 0) {
                            break;
                        }
                        if (countBytes != 4) {
                            throw new Exception("wrong key length format");
                        }
                        cast = ByteBuffer.wrap(buffer);
                        keyLength = cast.getInt();
                        countBytes = readBytes(inputStream, 4, buffer);
                        if (countBytes != 4) {
                            throw new Exception("wrong value length format");
                        }
                        cast = ByteBuffer.wrap(buffer);
                        valueLength = cast.getInt();
                        if (keyLength > maxLength || valueLength > maxLength) {
                            throw new Exception("key or value length is very big");
                        }
                        if (keyLength < 0 || valueLength < 0) {
                            throw  new Exception("lengths must is negative");
                        }
                        buffer = new byte[keyLength];
                        countBytes = readBytes(inputStream, keyLength, buffer);
                        if (countBytes != keyLength) {
                            throw new Exception("wrong key length");
                        }
                        key = new String(buffer, StandardCharsets.UTF_8);
                        buffer = new byte[valueLength];
                        countBytes = readBytes(inputStream, valueLength, buffer);
                        if (countBytes != valueLength) {
                            throw new Exception("wrong value length");
                        }
                        value = new String(buffer, StandardCharsets.UTF_8);
                        map[i][j].put(key, value);
                    }
                } catch (FileNotFoundException e) {
                    throw new Exception(e.getMessage());
                }
            }
            curDir = new File(appendPath(".."));
        }
        checkValidateData();
        curDir = new File(appendPath(".."));
    }

    private void dropTable(String tablename) throws Exception {
        File dropFile = new File(appendPath(tablename)).getAbsoluteFile();
        Path dropPath = dropFile.toPath();
        if (!dropFile.exists()) {
            throw new Exception("table doesn't exist");
        }
        File[] files = dropFile.listFiles();
        if (files != null) {
            for (File file : files) {
                String toRemove = file.getPath();
                dropTable(toRemove);
            }
        }
        try {
            if (!Files.deleteIfExists(dropPath)) {
                throw new Exception(dropFile.getName() + " can't be removed");
            }
        } catch (AccessDeniedException e) {
            throw new Exception("access denied");
        } catch (Exception a) {
            throw new Exception(a.getMessage());
        }
    }

    private void checkAmountArgs(int actual, int need) throws Exception {
        if (actual < need) {
            throw new Exception("Few arguments");
        } else if (actual > need) {
            throw new Exception("Too many arguments");
        }
    }

    private void cleanHashMap() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                map[i][j].clear();
            }
        }
    }


    private String appendPath(String path) throws Exception {
        File tmp = new File(path);
        String tmpStr = "";
        if (tmp.isAbsolute()) {
            return tmp.getAbsolutePath();
        } else {
            tmp = new File(curDir.getAbsolutePath() + File.separator + path);
            try {
                tmpStr = tmp.getCanonicalPath();
            } catch (IOException e) {
                throw new Exception("Can't get canonical path");
            }
            return tmpStr;
        }
    }

    public int readBytes(DataInputStream stream, int size, byte[] buffer) throws IOException {
        int length = 0;
        int readLength = 0;
        while (size > length) {
            readLength = stream.read(buffer, length, size - length);
            if (readLength == -1) {
                return length;
            }
            length += readLength;
        }
        return length;
    }

    public void checkDirValidate() throws Exception {
        if (!curDir.exists()) {
            throw new Exception("table doesn't exist");
        }
        File[] dirs = curDir.listFiles();
        if (dirs == null) {
            throw new Exception("Wrong path");
        }
        for (File dir : dirs) {
            if (!dir.getName().matches("((1[0-5])|[0-9])\\.dir")) {
                throw new Exception("invalid directory : found unexpected file or directory");
            }
            File[] files = dir.listFiles();
            if (files == null) {
                throw new Exception("Wrong path");
            }
            for (File file : files) {
                if (!file.getName().matches("((1[0-5])|[0-9])\\.dat")) {
                    throw new Exception("invalid directory : found unexpected file or directory");
                }
            }
        }
    }

    public void checkValidateData() throws Exception {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (String key : map[i][j].keySet()) {
                    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                    int firstByte = Math.abs(keyBytes[0]);
                    int nDirectory = firstByte % 16;
                    int nFile = firstByte / 16 % 16;
                    if (nDirectory != i || nFile != j) {
                        throw new Exception("Wrong data format : incorrect key location");
                    }
                }
            }
        }
    }
}
