package ru.fizteh.fivt.students.mazanovArtem.multifilehashmap;

import ru.fizteh.fivt.students.mazanovArtem.shell.Command;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;

public class FileMapSystem implements Command {
    private File curDir;
    HashMap<String, String> map;
    int maxLength;


    public File getFile() {
        return curDir;
    }

    public FileMapSystem(File dir) {
        if (dir == null) {
            throw new IllegalArgumentException("null direction");
        }
        curDir = dir;
        maxLength = 1 << 24;
        map = new HashMap<>();
        try {
            loadTable();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void exit() {
        try {
            saveTable();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String helloString() throws IOException {
        //return getFile().getCanonicalPath() + "$ ";
        return "";
    }

    @Override
    public Map<String, Object[]> linkCommand() {
        Map<String, Object[]> commandList = new HashMap<String, Object[]>(){ {
            put("put", new Object[] {"put", true, 2 });
            put("get", new Object[] {"get", false, 1 });
            put("remove", new Object[] {"remove", false, 1});
            //put("create", new Object[] {"create", false, 1 });
            //put("drop", new Object[] {"drop", false, 1 });
            //put("use", new Object[] {"use", false, 1 });
        }};
        return commandList;
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

    /*public void clear() {
        map.clear();
    } */

    public void put(String[] args) throws Exception {
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

        String tmp = map.get(key);
        if (tmp != null) {
            System.out.println("overwrite");
            System.out.println(tmp);
        } else {
            System.out.println("new");
        }
        map.put(key, value);
    }

    public void get(String[] args) throws Exception {
        checkAmountArgs(args.length, 1);
        String tmp = map.get(args[0]);
        if (tmp == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(tmp);
        }
    }

    public void remove(String[] args) throws Exception {
        checkAmountArgs(args.length, 1);
        String tmp = map.get(args[0]);
        if (tmp == null) {
            System.out.println("not found");
        } else {
            map.remove(args[0]);
            System.out.println("removed");
        }
    }

    /*public void create(String[] args) throws Exception {
        checkAmountArgs(args.length,2);
        System.out.println(args[1]);
        File tmpFile = new File(appendPath(args[1]));
        if (tmpFile.exists()) {
            System.out.println("tablename exist");
        } else {
            if (!tmpFile.mkdir()) {
                throw new Exception(String.format("Table %s can't be create", args[0]));
            } else {
                System.out.println("created");
            }
        }
    }*/

    /*public void drop(String[] args) throws Exception {
        checkAmountArgs(args.length,2);
        File tmpFile = new File(appendPath(args[1]));
        Data.setDir(curDir);
        if (tmpFile.exists()) {
            args[0] = "rm";
            Shell.main(args);
            System.out.println("dropped");
        } else {
            System.out.println("tablename not exist");
        }
        if (args[0].equals(Data.getName())) {
            cleanHashMap();
        }
        Data.setNameBool(false);
    } */

    /*public void use(String[] args) throws Exception {
        checkAmountArgs(args.length,2);
        File tmpFile = new File(appendPath(args[1]));
        if (tmpFile.exists()) {
            saveTable(Data.getName());
            Data.setName(args[1]);
            Data.setNameBool(true);
            loadTable(args[1]);
            System.out.println("using tablename");
        } else {
            System.out.println("tablename not exists");
        }
    }*/

    private void checkAmountArgs(int actual, int need) throws Exception {
        if (actual < need) {
            throw new Exception("Few arguments");
        } else if (actual > need) {
            throw new Exception("Too many arguments");
        }
    }

    /*private String appendPath(String path) {
        File tmp = new File(path);
        if (tmp.isAbsolute()) {
            return tmp.getAbsolutePath();
        } else {
            return curDir.getAbsolutePath() + File.separator + path;
        }
    }*/

    private void saveTable() throws Exception {
        if (curDir.exists() && curDir.isDirectory()) {
            throw new Exception("Can't be saved in directory");
        }
        try {
            if (!curDir.exists()) {
                if (!curDir.createNewFile()) {
                    throw new Exception("Can't be written there,because can't create file");
                }
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(curDir))) {
            for (String keys : map.keySet()) {
                byte[] key = keys.getBytes(StandardCharsets.UTF_8);
                byte[] value = map.get(keys).getBytes(StandardCharsets.UTF_8);
                outputStream.write(ByteBuffer.allocate(4).putInt(key.length).array());
                outputStream.write(ByteBuffer.allocate(4).putInt(value.length).array());
                outputStream.write(key);
                outputStream.write(value);
            }
        } catch (FileNotFoundException e) {
            throw new Exception("File not found", e);
        }
    }

    private void loadTable() throws Exception {
        map.clear();
        if (!curDir.getParentFile().exists() || !curDir.getParentFile().isDirectory()) {
            throw new Exception("directory doesn't exist");
        }
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(curDir))) {
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
                map.put(key, value);
            }
        } catch (FileNotFoundException e) {
            throw new Exception(e.getMessage());
        }
    }



    /*private void cleanHashMap() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                mapCont[i][j].clear();
            }
        }
    }*/
}
