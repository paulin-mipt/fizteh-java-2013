package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.tools.ColumnTypes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class FileManager {

    private static class LineOfDB {
        public long length = 0;
        public String key = "";
        public String value = "";

        public LineOfDB() {
        }

        public LineOfDB(long length, String key, String value) {
            this.length = length;
            this.key = key;
            this.value = value;
        }
    }

    private static void checkKeyOnRightHashCode(String key, int originalDirIndex, int originalFileIndex)
            throws IOException {
        int hashCode = key.hashCode();
        int indexOfDir = hashCode % 16;
        if (indexOfDir < 0) {
            indexOfDir *= -1;
        }
        int indexOfDat = hashCode / 16 % 16;
        if (indexOfDat < 0) {
            indexOfDat *= -1;
        }
        if (indexOfDir != originalDirIndex || indexOfDat != originalFileIndex) {
            throw new IOException(String.format("wrong key '%s': it should be in %d.dir in %d.dat, "
                    + "but it is in %d.dir in %d.dat", key, indexOfDir,
                    indexOfDat, originalDirIndex, originalFileIndex));
        }
    }

    private static LineOfDB readLineOfDatFile(RandomAccessFile datFile, long inputLength,
                                              int dirIndex, int fileIndex) throws IOException {
        long length = inputLength;
        int lengthOfKey;
        int lengthOfValue;
        LineOfDB empty = new LineOfDB();
        try {
            lengthOfKey = datFile.readInt();
            lengthOfValue = datFile.readInt();
        } catch (IOException e) {
            throw new IOException("Wrong format of db: " + e.getMessage());
        }
        length -= 8;
        if (lengthOfKey <= 0 || lengthOfValue <= 0) {
            throw new IOException("Wrong format of db: length of key and length of value must be positive integers.");
        }
        if (lengthOfKey > length) {
            throw new IOException("Wrong format of db: length of key ​​do not match content.");
        }
        byte[] bytesOfKey = readKeyOrValue(datFile, lengthOfKey);
        length -= lengthOfKey;
        if (lengthOfValue > length) {
            System.err.println("Wrong format of db: length of value ​​do not match content.");
            return empty;
        }
        byte[] bytesOfValue = readKeyOrValue(datFile, lengthOfValue);
        length -= lengthOfValue;
        String key;
        String value;
        try {
            key = new String(bytesOfKey, "UTF-8");
            value = new String(bytesOfValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
            return empty;
        }
        if (key.contains("\\s")) {
            throw new IOException("key contains whitespace symbol");
        }
        checkKeyOnRightHashCode(key, dirIndex, fileIndex);
        long lengthOfLine = inputLength - length;
        return new LineOfDB(lengthOfLine, key, value);
    }

    private static byte[] readKeyOrValue(RandomAccessFile datFile, int length) throws IOException {
        byte[] bytes = new byte[length];
        int countOfBytesWasRead = 0;
        while (true) {
            try {
                countOfBytesWasRead = datFile.read(bytes, countOfBytesWasRead, length - countOfBytesWasRead);
            } catch (IOException e) {
                throw new IOException("Wrong format of db: " + e.getMessage());
            }
            if (countOfBytesWasRead == length) {
                break;
            }
            if (countOfBytesWasRead == -1) {
                throw new IOException("Error while reading key or value");
            }
        }
        return bytes;
    }

    private static void readFromDisk(RandomAccessFile datFile, HashMap<String, String> storage,
                                     int dirIndex, int fileIndex) throws IOException {
        long length;
        length = datFile.length();
        if (length == 0) {
            return;
        }
        while (length > 0) {
            LineOfDB line = readLineOfDatFile(datFile, length, dirIndex, fileIndex);
            length -= line.length;
            storage.put(line.key, line.value);
        }
        datFile.close();
    }

    public static void readDBFromDisk(File tableDirectory, HashMap<String, String> tableStorage) throws IOException {
        if (!tableDirectory.exists() || tableDirectory.isFile()) {
            throw new IOException(tableDirectory + ": not directory or not exist");
        }
        for (int index = 0; index < 16; index++) {
            String currentDirectoryName = index + ".dir";
            File currentDirectory = new File(tableDirectory, currentDirectoryName);
            if (!currentDirectory.exists()) {
                continue;
            }
            if (!currentDirectory.isDirectory()) {
                throw new IOException(currentDirectory.toString() + ": not directory");
            }
            for (int fileIndex = 0; fileIndex < 16; fileIndex++) {
                String currentFileName = fileIndex + ".dat";
                File currentFile = new File(currentDirectory, currentFileName);
                if (!currentFile.exists()) {
                    continue;
                }
                try (RandomAccessFile fileIndexDat = new RandomAccessFile(currentFile, "rw")) {
                    readFromDisk(fileIndexDat, tableStorage, index, fileIndex);
                } catch (FileNotFoundException e) {
                    continue;
                }
            }
        }
    }

    private static void cleanEmptyDir(File dir) throws IOException {
        if (dir.exists()) {
            if (dir.listFiles().length == 0) {
                if (!dir.delete()) {
                    throw new IOException("File: " + dir.toString() + " can't be deleted");
                }
            }
        }
    }

    private static void parseStorage(HashMap<String, String>[][] parsedStorage, HashMap<String, String> storage) {
        for (String key : storage.keySet()) {
            int hashCode = key.hashCode();
            int indexOfDir = hashCode % 16;
            if (indexOfDir < 0) {
                indexOfDir *= -1;
            }
            int indexOfDat = hashCode / 16 % 16;
            if (indexOfDat < 0) {
                indexOfDat *= -1;
            }
            if (parsedStorage[indexOfDir][indexOfDat] == null) {
                parsedStorage[indexOfDir][indexOfDat] = new HashMap<>();
            }
            parsedStorage[indexOfDir][indexOfDat].put(key, storage.get(key));
        }
    }

    public static void writeToDatFile(RandomAccessFile datFile, HashMap<String, String> storage) throws IOException {
        try {
            datFile.seek(0);
            datFile.setLength(0);
            for (String key : storage.keySet()) {
                byte[] bytesOfKey = key.getBytes("UTF-8");
                byte[] bytesOfValue = storage.get(key).getBytes("UTF-8");
                datFile.writeInt(bytesOfKey.length);
                datFile.writeInt(bytesOfValue.length);
                datFile.write(bytesOfKey);
                datFile.write(bytesOfValue);
            }
        } catch (Exception e) {
            try {
                datFile.close();
            } catch (Exception e2) {
                throw new IOException(e + "\n" + e2);
            }
        }
    }

    public static void writeTableOnDisk(File tableDirectory, HashMap<String, String> tableStorage) throws IOException {
        if (tableDirectory == null) {
            throw new IOException("Error! You try to write in null file.");
        }
        HashMap<String, String>[][] parsedStorage = new HashMap[16][16];
        parseStorage(parsedStorage, tableStorage);
        for (int indexOfDir = 0; indexOfDir < 16; indexOfDir++) {
            File dir = new File(tableDirectory, indexOfDir + ".dir");
            for (int indexOfDatFile = 0; indexOfDatFile < 16; indexOfDatFile++) {
                File datFile = new File(dir, indexOfDatFile + ".dat");
                if (parsedStorage[indexOfDir][indexOfDatFile] == null) {
                    if (datFile.exists()) {
                        if (!datFile.delete()) {
                            throw new IOException("File: " + datFile.toString() + " can't be deleted");
                        }
                    }
                    continue;
                }
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        throw new IOException("Directory  " + dir.toString() + " can't be created");
                    }
                }
                if (!datFile.exists()) {
                    if (!datFile.createNewFile()) {
                        throw new IOException("File " + datFile.toString() + " can't be created");
                    }
                }
                try (RandomAccessFile currentFile = new RandomAccessFile(datFile, "rw")) {
                    writeToDatFile(currentFile, parsedStorage[indexOfDir][indexOfDatFile]);
                } catch (FileNotFoundException e) {
                    throw new IOException("This error is my fail. Check 'writeTableOnDisk' function");
                }
            }
            cleanEmptyDir(dir);
        }
    }

    public static List<Class<?>> readTableSignature(File tableDirectory) throws IOException {
        if (!tableDirectory.exists()) {
            throw new IllegalArgumentException("not existed table");
        }
        File signature = new File(tableDirectory, "signature.tsv");
        if (!signature.exists()) {
            throw new IOException("signature file not exist: " + signature.getCanonicalPath());
        }
        Scanner scan = new Scanner(signature);
        if (!scan.hasNext()) {
            throw new IOException("empty signature: " + signature.getCanonicalPath());
        }
        String[] types = scan.nextLine().split(" ");
        ColumnTypes ct = new ColumnTypes();
        List<Class<?>> listOfTypes = ct.convertArrayOfStringsToListOfClasses(types);
        scan.close();
        return listOfTypes;
    }

    public static void writeSignature(File tableDirectory, List<String> columnTypes) throws IOException {
        if (!tableDirectory.exists()) {
            throw new IllegalArgumentException("not existed table");
        }
        if (columnTypes == null || columnTypes.size() == 0) {
            throw new IllegalArgumentException("null or empty list of column types");
        }
        File signature = new File(tableDirectory, "signature.tsv");
        if (!signature.createNewFile()) {
            throw new IOException("failed to create new signature.tsv: probably a file with such name already exists");
        }

        try (RandomAccessFile signatureFile = new RandomAccessFile(signature, "rw")) {
            for (int i = 0; i < columnTypes.size(); i++) {
                String type = columnTypes.get(i);
                if (type.matches("int|byte|long|float|double|boolean|String")) {
                    signatureFile.write(type.getBytes(StandardCharsets.UTF_8));
                    if (i != (columnTypes.size() - 1)) {
                        signatureFile.write(' ');
                    }
                } else {
                    signatureFile.close();
                    signature.delete();
                    throw new IOException("writing signature: illegal type: " + type);
                }
            }
        }
    }

    public static void checkTableDir(File tableDir) throws IOException {
        if (!tableDir.exists()) {
            throw new IOException(String.format("DBTable: table dir %s does not exist", tableDir));
        }
        File[] listFiles = tableDir.listFiles();
        if (listFiles == null) {
            throw new IOException(String.format("DBTable: file %s is not a dir", tableDir));
        }
        if (listFiles.length == 0) {
            throw new IOException("empty dir");
        }
        for (File dirFile : listFiles) {
            if (dirFile.isDirectory()) {
                if (!dirFile.getName().matches("(0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15)\\.dir")) {
                    throw new IOException(String.format("illegal name of dir %s inside table %s",
                            dirFile.getName(), tableDir.getName()));
                } else {
                    File[] listFilesInsideDir = dirFile.listFiles();
                    if (listFilesInsideDir.length == 0) {
                        throw new IOException("empty dir " + dirFile.getName());
                    }
                    for (File datFiles : listFilesInsideDir) {
                        if (!datFiles.getName().matches("(0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15)\\.dat")) {
                            throw new IOException(String.format("illegal name of file %s inside dir %s inside table %s",
                                    datFiles.getName(), dirFile.getName(), tableDir.getName()));
                        } else {
                            if (datFiles.length() == 0) {
                                throw new IOException("empty file " + datFiles.getName());
                            }
                        }
                    }
                }
            } else {
                if (!dirFile.getName().equals("signature.tsv")) {
                    throw new IOException("illegal file " + dirFile.getName());
                }
            }
        }
    }
}
