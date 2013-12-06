package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AbstractFileMap extends AbstractFrame<FileMapState> {
    private static Map<String, String> content = new HashMap<String, String>();
    private static final int MAX_FILE_SIZE = 150100500;
    private static RandomAccessFile file;

    public AbstractFileMap(File file) throws IOException {
        state = new FileMapState();
        state.setCurState(file);
        checkOpenFile();
    }

    public static RandomAccessFile getFile() {
        return file;
    }

    public static Map<String, String> getContent() {
        return content;
    }

    @Override
    public Map<String, AbstractCommand> getCommands() {
        final FileMapPutCommand put = new FileMapPutCommand();
        final FileMapGetCommand get = new FileMapGetCommand();
        final FileMapRemoveCommand remove = new FileMapRemoveCommand();
        final FileMapExitCommand exit = new FileMapExitCommand();

        return new HashMap<String, AbstractCommand>() {
            {
                put(put.getCmdName(), put);
                put(get.getCmdName(), get);
                put(remove.getCmdName(), remove);
                put(exit.getCmdName(), exit);
            }
        };
    }

    public void checkOpenFile() throws IOException {
        if (state.getCurState().exists()) {
            try {
                file = new RandomAccessFile(state.getCurState(), "rw");

                if (file.length() > MAX_FILE_SIZE) {
                    file.close();
                    throw new IOException("ERROR: too big file");
                }

                Map<String, String> map = readFile(file);
                putMap(map);
            } catch (IOException e) {
                file.close();
                System.err.println("ERROR: incorrect file format");
            }
        } else {
            if (!state.getCurState().createNewFile()) {
                throw new IOException("ERROR: cannot create db.dat");
            } else {
                checkOpenFile();
            }
        }
    }

    public static Map<String, String> readFile(RandomAccessFile file) throws IOException {
        Map<String, String> map = new HashMap<String, String>();

        if (file.length() == 0) {
            return null;
        }

        List<Integer> offsets = new ArrayList<Integer>();

        while (file.getFilePointer() != file.length()) {
            if (file.readByte() == '\0') {
                int offset = file.readInt();

                if (offset < 0 || offset > file.length()) {
                    file.close();
                    throw new IOException("ERROR: incorrect input");
                }

                offsets.add(offset);
            }
        }
        offsets.add((int) file.length());

        file.seek(0);

        for (int i = 0; i < offsets.size() - 1; ++i) {
            List<Byte> bytesKeyList = new ArrayList<Byte>();

            while (file.getFilePointer() != file.length()) {
                byte b = file.readByte();

                if (b == 0) {
                    break;
                }

                bytesKeyList.add(b);
            }

            byte[] bytesKeyArray = new byte[bytesKeyList.size()];

            for (int j = 0; j < bytesKeyArray.length; ++j) {
                bytesKeyArray[j] = bytesKeyList.get(j);
            }

            String key = new String(bytesKeyArray, StandardCharsets.UTF_8);

            file.read();
            file.readInt();

            int currentOffset = (int) file.getFilePointer() - 1;

            file.seek(offsets.get(i));

            byte[] valueArray = new byte[offsets.get(i + 1) - offsets.get(i)];

            file.read(valueArray);

            String value = new String(valueArray, StandardCharsets.UTF_8);

            map.put(key, value);
            file.seek(currentOffset);
        }

        return map;
    }

    private void putMap(Map<String, String> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                content.put(key, map.get(key));
            }
        }
    }

    public static void commitFile(RandomAccessFile file, Set<String> keySet, Map<String, String> content)
            throws IOException {
        file.setLength(0);

        int curOffset = 0;
        int position = 0;

        for (String key : keySet) {
            curOffset += key.getBytes(StandardCharsets.UTF_8).length + 5;
        }

        for (String key : keySet) {
            file.seek(position);
            file.write(key.getBytes(StandardCharsets.UTF_8));
            file.write('\0');
            file.writeInt(curOffset);
            position = (int) file.getFilePointer();
            file.seek(curOffset);
            file.write(content.get(key).getBytes(StandardCharsets.UTF_8));
            curOffset = (int) file.getFilePointer();
        }
    }
}
