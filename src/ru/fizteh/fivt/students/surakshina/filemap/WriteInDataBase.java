package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WriteInDataBase {
    public static void saveFile(File tmp, HashMap<String, String> map) throws IOException {
        RandomAccessFile dataBase = null;
        try {
            dataBase = new RandomAccessFile(tmp.toString(), "rw");
            int lengthOfkeys = 0;
            Set<Map.Entry<String, String>> set = map.entrySet();
            for (Map.Entry<String, String> entry : set) {
                lengthOfkeys += (entry.getKey().getBytes(StandardCharsets.UTF_8).length + 1 + 4);
            }
            dataBase.setLength(0);
            dataBase.seek(0);
            for (Map.Entry<String, String> myEntry : set) {
                dataBase.write(myEntry.getKey().getBytes(StandardCharsets.UTF_8));
                dataBase.writeByte(0);
                dataBase.writeInt(lengthOfkeys);
                lengthOfkeys += myEntry.getValue().getBytes(StandardCharsets.UTF_8).length;
            }
            for (Map.Entry<String, String> myEntry : set) {
                dataBase.write(myEntry.getValue().getBytes(StandardCharsets.UTF_8));
            }
        } finally {
            try {
                CloseFile.closeFile(dataBase);
            } catch (Throwable e) {
                // It is ok
            }
        }
    }
}
