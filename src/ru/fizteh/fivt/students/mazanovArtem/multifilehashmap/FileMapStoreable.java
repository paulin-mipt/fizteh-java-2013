package ru.fizteh.fivt.students.mazanovArtem.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class FileMapStoreable implements Table {
    private File curDir;
    //HashMap<String, String>[][] map = new HashMap[16][16];
    //HashMap<String, String>[][] mapChanges = new HashMap[16][16];
    HashMap<String, String> map;
    HashMap<String, String> mapChanges;
    int maxLength;
    String nameUseTable;


    public String getName() {
        return nameUseTable;
    }

    public FileMapStoreable(File dir,String name) throws Exception {
        if (dir == null) {
            throw new IllegalArgumentException("null direction");
        }
        curDir = dir;
        maxLength = 1 << 24;
        map = new HashMap<>();
        mapChanges = new HashMap<>();
        nameUseTable = name;
        loadTable();
    }

    public String put(String key, String value) throws IllegalStateException {
        if (key == null || value == null) {
            throw new IllegalStateException("put : wrong key or data(null)");
        }
        String answer = null;
        if (mapChanges.containsKey(key)) {
            if (map.containsKey(key)) {
                if (mapChanges.get(key).equals(value)) {
                    answer = value;
                } else {
                    answer = mapChanges.get(key);
                    if (map.get(key).equals(value)) {
                        mapChanges.remove(key);
                    } else {
                        mapChanges.put(key,value);
                    }
                }
            } else {
                answer = mapChanges.get(key);
                mapChanges.put(key,value);
            }
        } else {
            if (map.containsKey(key)) {
                if (map.get(key).equals(value)) {
                    answer = value;
                } else {
                    mapChanges.put(key, value);
                }
            } else {
                mapChanges.put(key, value);
            }
        }
        return answer;
    }

    public String remove(String key) {
        if (key == null) {
            throw new IllegalStateException("remove : key is null");
        }
        String answer = null;
        if (mapChanges.containsKey(key)) {
            if (map.containsKey(key)) {
                if (mapChanges.get(key) != null) {
                    answer = mapChanges.get(key);
                    mapChanges.put(key, null);
                }
            } else {
                answer = mapChanges.get(key);
                mapChanges.remove(key);
            }
        } else {
            if (map.containsKey(key)) {
                answer = map.get(key);
                mapChanges.put(key, null);
            }
        }
        return answer;
    }

    public String get(String key) throws IllegalStateException {
        if (key == null) {
            throw new IllegalStateException("get : key is null");
        }
        String answer = null;
        if (mapChanges.containsKey(key)) {
            answer = mapChanges.get(key);
        } else {
            if (map.containsKey(key)) {
                answer = mapChanges.get(key);
            }
        }
        return answer;
    }

    public int size() {
        int s = map.size();
        for (String key : mapChanges.keySet()) {
            if (map.containsKey(key)) {
                if (mapChanges.get(key) == null) {
                    s--;
                }
            } else {
                if (mapChanges.get(key) != null) {
                    s++;
                }
            }
        }
        return s;
    }

    public int commit() {
        int s = sizeChanges();
        for (String key : mapChanges.keySet()) {
            boolean mapcontkey = map.containsKey(key);
            String value = map.get(key);
            String changeValue = mapChanges.get(key);
            if (mapcontkey) {
                if (changeValue != null) {
                    if (!changeValue.equals(value)) {
                        map.put(key, changeValue);
                    }
                } else {
                    map.remove(key);
                }
            } else {
                if (changeValue != null) {
                    map.put(key,changeValue);
                }
            }
        }
        try {
            saveTable();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        mapChanges.clear();
        return s;
    }

    public int rollback() {
        int s = sizeChanges();
        mapChanges.clear();
        return s;
    }

    public int sizeChanges() {
        int s = 0;
        for (String key : mapChanges.keySet()) {
            if (map.containsKey(key)) {
                if (mapChanges.get(key) == null) {
                    s++;
                }
            } else {
                if (!mapChanges.get(key).equals(map.get(key))) {
                    s++;
                }
            }
        }
        return s;
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

    public void checkDirValidate(File dirDb) throws Exception {
        if (!dirDb.exists()) {
            throw new Exception("table doesn't exist");
        }
        File[] dirs = dirDb.listFiles();
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

    public void checkValidateData(HashMap<String, String>[][] mapa) throws Exception {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (String key : mapa[i][j].keySet()) {
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

    public void saveTable() throws Exception {
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
        HashMap<String, String>[][] tmpMap  = new HashMap[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                tmpMap[i][j] = new HashMap<>();
            }
        }
        for (String key : map.keySet()) {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            int firstByte = Math.abs(keyBytes[0]);
            int nDirectory = firstByte % 16;
            int nFile = firstByte / 16 % 16;
            tmpMap[nDirectory][nFile].put(key, map.get(key));
        }
        curDir = tmpFile;
        for (int i = 0; i < 16; ++i) {
            tmpFile = new File(appendPath(String.valueOf(i) + ".dir"));
            for (int j = 0; j < 16; ++j) {
                if (!tmpMap[i][j].isEmpty()) {
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
                if (tmpMap[i][j].isEmpty()) {
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
                if (tmpMap[i][j].isEmpty()) {
                    continue;
                }
                //System.out.println(tmpFile + " saveTable");
                try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(tmpFile))) {
                    for (String keys : tmpMap[i][j].keySet()) {
                        byte[] key = keys.getBytes(StandardCharsets.UTF_8);
                        byte[] value = tmpMap[i][j].get(keys).getBytes(StandardCharsets.UTF_8);
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
        map.clear();
        if (!curDir.getParentFile().exists() || !curDir.getParentFile().isDirectory()) {
            throw new Exception("directory doesn't exist");
        }
        File tmpFile = new File(appendPath(nameUseTable));
        curDir = tmpFile;
        checkDirValidate(curDir);
        HashMap<String, String>[][] tmpMap  = new HashMap[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                tmpMap[i][j] = new HashMap<>();
            }
        }
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
                        tmpMap[i][j].put(key, value);
                    }
                } catch (FileNotFoundException e) {
                    throw new Exception(e.getMessage());
                }
            }
            curDir = new File(appendPath(".."));
        }
        checkValidateData(tmpMap);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (String key : tmpMap[i][j].keySet()) {
                    map.put(key, tmpMap[i][j].get(key));
                }
            }
        }
        curDir = new File(appendPath(".."));
    }

    public void dropTable(String tablename) throws Exception {
        File dropFile = new File(appendPath(tablename)).getAbsoluteFile();
        Path dropPath = dropFile.toPath();
        if (!dropFile.exists()) {
            throw new Exception(tablename + " doesn't exist");
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
}
