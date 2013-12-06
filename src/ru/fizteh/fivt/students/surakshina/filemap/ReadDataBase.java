package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import ru.fizteh.fivt.storage.structured.Storeable;

public class ReadDataBase {
    public static HashMap<String, Storeable> loadFile(File file, NewTable table) throws IOException, ParseException {
        HashMap<String, Storeable> mapFile = new HashMap<>();
        try (RandomAccessFile dataBase = new RandomAccessFile(file, "rw")) {
            String keyFirst;
            String keySecond = null;
            String value;
            int currentPosition = (int) dataBase.getFilePointer();
            int offsetOfValueFirst = 0;
            int firstOffset = 0;
            int offsetOfValueSecond = 0;
            dataBase.seek(currentPosition);

            byte c = 0;
            ArrayList<Byte> vector = new ArrayList<Byte>();
            c = dataBase.readByte();
            while (c != 0) {
                vector.add(c);
                c = dataBase.readByte();
            }
            byte[] res = new byte[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                res[i] = vector.get(i).byteValue();
            }
            keyFirst = new String(res, StandardCharsets.UTF_8);

            offsetOfValueFirst = dataBase.readInt();
            if (offsetOfValueFirst > 0) {
                currentPosition = (int) dataBase.getFilePointer();
                firstOffset = offsetOfValueFirst;
                do {
                    dataBase.seek(currentPosition);
                    if (currentPosition < firstOffset) {
                        c = 0;
                        vector.clear();
                        c = dataBase.readByte();
                        while (c != 0) {
                            vector.add(c);
                            c = dataBase.readByte();
                        }
                        res = new byte[vector.size()];
                        for (int i = 0; i < vector.size(); i++) {
                            res[i] = vector.get(i).byteValue();
                        }
                        keySecond = new String(res, StandardCharsets.UTF_8);
                        offsetOfValueSecond = dataBase.readInt();
                        currentPosition = (int) dataBase.getFilePointer();
                    } else if (currentPosition == offsetOfValueFirst) {
                        offsetOfValueSecond = (int) dataBase.length();
                        ++currentPosition;
                    }
                    dataBase.seek(firstOffset);
                    int first = (int) dataBase.getFilePointer();
                    byte[] tmp;
                    if (offsetOfValueSecond != first) {
                        tmp = new byte[(int) (offsetOfValueSecond - first)];
                        dataBase.read(tmp);
                    } else {
                        tmp = new byte[(int) (dataBase.length() - offsetOfValueSecond)];
                        dataBase.read(tmp);
                    }
                    value = new String(tmp, StandardCharsets.UTF_8);
                    byte firstC = 0;
                    firstC = (byte) Math.abs(keyFirst.getBytes(StandardCharsets.UTF_8)[0]);
                    int ndirectory = firstC % 16;
                    int nfile = firstC / 16 % 16;
                    String dat = file.getName().substring(0, file.getName().lastIndexOf("."));
                    String dir = file.getParentFile().getName()
                            .substring(0, file.getParentFile().getName().lastIndexOf("."));
                    if (!dat.equals(Integer.class.cast(nfile).toString())
                            || !dir.equals(Integer.class.cast(ndirectory).toString())) {
                        throw new IOException("wrong key placement");
                    }
                    mapFile.put(keyFirst, table.getTableProvider().deserialize(table, value));
                    keyFirst = keySecond;
                    firstOffset = offsetOfValueSecond;
                } while (currentPosition < offsetOfValueFirst);
                if (keyFirst != null) {
                    int first = (int) dataBase.getFilePointer();
                    byte[] tmp;
                    if (offsetOfValueSecond != first) {
                        tmp = new byte[(int) (offsetOfValueSecond - first)];
                        dataBase.read(tmp);
                    } else {
                        tmp = new byte[(int) (dataBase.length() - offsetOfValueSecond)];
                        dataBase.read(tmp);
                    }
                    value = new String(tmp, StandardCharsets.UTF_8);
                    byte firstC = 0;
                    firstC = (byte) Math.abs(keyFirst.getBytes(StandardCharsets.UTF_8)[0]);
                    int ndirectory = firstC % 16;
                    int nfile = firstC / 16 % 16;
                    String dat = file.getName().substring(0, file.getName().lastIndexOf("."));
                    String dir = file.getParentFile().getName()
                            .substring(0, file.getParentFile().getName().lastIndexOf("."));
                    if (!dat.equals(Integer.class.cast(nfile).toString())
                            || !dir.equals(Integer.class.cast(ndirectory).toString())) {
                        throw new IOException("wrong key placement");
                    }
                    mapFile.put(keyFirst, table.getTableProvider().deserialize(table, value));
                }
            } else {
                throw new IOException("Offset is negative");
            }

        }
        return mapFile;
    }
}
