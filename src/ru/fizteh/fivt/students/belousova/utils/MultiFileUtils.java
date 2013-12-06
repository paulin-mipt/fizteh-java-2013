package ru.fizteh.fivt.students.belousova.utils;

import ru.fizteh.fivt.students.belousova.multifilehashmap.IsKeyValid;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultiFileUtils {
    public static void read(File directory, Map<String, String> map) throws IOException {
        if (!directory.exists()) {
            throw new IOException("directory doesn't exist");
        }
        if (!directory.isDirectory()) {
            throw new IOException("'" + directory.getName() + "' is not a directory");
        }

        for (int ndirectory = 0; ndirectory < 16; ndirectory++) {
            String subDirName = ndirectory + ".dir";
            File subDirectory = new File(directory, subDirName);

            if (!subDirectory.exists()) {
                continue;
            }
            if (!subDirectory.isDirectory()) {
                throw new IOException("'" + subDirectory.toString() + "' is not a directory");
            }

            for (int nfile = 0; nfile < 16; nfile++) {
                String fileName = nfile + ".dat";
                File dataFile = new File(subDirectory, fileName);
                if (!dataFile.exists()) {
                    continue;
                }
                Predicate<String> predicate = new IsKeyValid(nfile, ndirectory);
                FileMapUtils.read(dataFile, map, predicate);
            }
        }
    }

    public static void write(File directory, Map<String, String> map) throws IOException {
        Map<String, String>[][] mapArray = new Map[16][16];
        for (String key : map.keySet()) {
            int keyByte = Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
            int nDirectory = keyByte % 16;
            int nFile = keyByte / 16 % 16;
            if (mapArray[nDirectory][nFile] == null) {
                mapArray[nDirectory][nFile] = new HashMap<String, String>();
            }
            mapArray[nDirectory][nFile].put(key, map.get(key));
        }

        for (int i = 0; i < 16; i++) {
            File dataDirectory = new File(directory, i + ".dir");
            for (int j = 0; j < 16; j++) {
                File dataFile = new File(dataDirectory, j + ".dat");
                if (mapArray[i][j] == null) {
                    if (dataFile.exists()) {
                        dataFile.delete();
                    }
                    continue;
                }

                if (!dataDirectory.exists()) {
                    dataDirectory.mkdir();
                }

                if (!dataFile.exists()) {
                    dataFile.createNewFile();
                }
                FileMapUtils.write(dataFile, mapArray[i][j]);
            }
            if (dataDirectory.exists()) {
                if (dataDirectory.listFiles().length == 0) {
                    FileUtils.deleteDirectory(dataDirectory);
                }
            }
        }
    }
}
