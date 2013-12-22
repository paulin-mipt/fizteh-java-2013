package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.filemap.FileMapState;
import ru.fizteh.fivt.students.ermolenko.filemap.FileMapUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapUtils {

    public static void read(File currentDir, Map<String, String> currentMap) throws IOException {

        if (!currentDir.exists()) {
            throw new IOException("directory doesn't exist");
        }
        if (!currentDir.isDirectory()) {
            throw new IOException("'" + currentDir.getName() + "' is not a directory");
        }

        for (int directNumber = 0; directNumber < 16; ++directNumber) {
            File subDir = new File(currentDir, directNumber + ".dir");
            if (!subDir.exists()) {
                continue;
            }
            if (!subDir.isDirectory()) {
                throw new IOException(subDir.getName() + "isn't directory");
            }
            if (!(subDir.list().length > 0)) {
                throw new IOException("empty dir");
            }

            for (int fileNumber = 0; fileNumber < 16; ++fileNumber) {
                File currentFile = new File(subDir, fileNumber + ".dat");
                if (!currentFile.exists()) {
                    continue;
                }
                if (!(currentFile.length() > 0)) {
                    throw new IOException("empty file");
                }
                FileMapState state = new FileMapState(currentFile);
                state.setDataBase(currentMap);
                FileMapUtils.readAndCheckDataBase(state, directNumber, fileNumber);
            }
        }
    }

    public static void deleteDirectory(File directory) throws IOException {

        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteDirectory(f);
            }
        }
        boolean success = directory.delete();
        if (!success) {
            throw new IOException("cannot remove " + directory.getName() + ": unknown error");
        }
    }

    public static void write(File currentDir, Map<String, String> currentMap) throws IOException {

        Map<String, String>[][] arrayOfMap = new Map[16][16];
        for (String key : currentMap.keySet()) {
            int byteOfKey = key.getBytes(StandardCharsets.UTF_8)[0];
            int nDirectory = Math.abs(byteOfKey) % 16;
            int nFile = Math.abs(byteOfKey) / 16 % 16;
            if (arrayOfMap[nDirectory][nFile] == null) {
                arrayOfMap[nDirectory][nFile] = new HashMap<String, String>();
            }
            arrayOfMap[nDirectory][nFile].put(key, currentMap.get(key));
        }

        for (int i = 0; i < 16; i++) {
            File dir = new File(currentDir, i + ".dir");
            for (int j = 0; j < 16; j++) {
                File file = new File(dir, j + ".dat");
                if (null == arrayOfMap[i][j]) {
                    if (file.exists()) {
                        file.delete();
                    }
                    continue;
                }

                if (!dir.exists()) {
                    dir.mkdir();
                }

                if (!file.exists()) {
                    file.createNewFile();
                }
                FileMapUtils.write(arrayOfMap[i][j], file);
            }

            if (dir.exists()) {
                if (dir.listFiles().length == 0) {
                    deleteDirectory(dir);
                }
            }
        }
    }
}
