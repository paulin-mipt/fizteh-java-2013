package ru.fizteh.fivt.students.vyatkina.database.superior;


import ru.fizteh.fivt.students.vyatkina.FileManager;
import ru.fizteh.fivt.students.vyatkina.TimeToFinishException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class TableProviderUtils implements TableProviderConstants {

    public static Path createFileForKeyIfNotExists(String key, Path tablePath) throws IOException {
        byte keyByte = (byte) Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        int ndirectory = keyByte % NUMBER_OF_DIRECTORIES;
        int nfile = (keyByte / NUMBER_OF_DIRECTORIES) % NUMBER_OF_FILES;

        Path directory = tablePath.resolve(Paths.get(ndirectory + DOT_DIR));
        if (Files.notExists(directory)) {
            Files.createDirectory(directory);
        }
        Path file = directory.resolve(Paths.get(nfile + DOT_DAT));
        if (Files.notExists(file)) {
            Files.createFile(file);
        }
        return file;

    }

    public static Path fileForKey(String key, Path tablePath) {
        byte keyByte = (byte) Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        int ndirectory = keyByte % NUMBER_OF_DIRECTORIES;
        int nfile = (keyByte / NUMBER_OF_DIRECTORIES) % NUMBER_OF_FILES;

        Path directory = tablePath.resolve(Paths.get(ndirectory + DOT_DIR));
        Path file = directory.resolve(Paths.get(nfile + DOT_DAT));
        return file;
    }

    public static Set<Path> deleteFilesThatChanged(Path tablePath, Set<String> keysThatValuesHaveChanged) throws IOException {
        Set<Path> paths = new HashSet<>();
        if (Files.notExists(tablePath)) {
            Files.createDirectory(tablePath);
        }
        for (String key : keysThatValuesHaveChanged) {
            paths.add(TableProviderUtils.deleteFileForKey(key, tablePath));
        }
        return paths;
    }

    public static Path deleteFileForKey(String key, Path tablePath) throws IOException {
        Path fileForKey = fileForKey(key, tablePath);
        Files.deleteIfExists(fileForKey);
        return fileForKey;
    }

    public static Map<String, String> getTableFromDisk(File tableDirectory) throws IOException {
        Map<String, String> result = new HashMap<>();

        File[] directories = tableDirectory.listFiles();

        if (directories == null) {
            return result;
        }

        for (File directory : directories) {

            if (directory.isFile()) {
                if (directory.getName().equals(SIGNATURE_FILE) || directory.getName().equals(MAC_DS_FILE)) {
                    continue;
                } else {
                    throw new WrappedIOException("Some file in database: " + directory);
                }
            }

            if (!TableProviderChecker.isValidDatabaseDirectoryName(directory.getName())) {
                throw new WrappedIOException("Invalid database name: " + directory.getName());
            }
            File[] files = directory.listFiles();

            if (files == null) {
                throw new WrappedIOException(TableProviderConstants.EMPTY_DIRECTORY);
            }

            boolean haveFiles = false;

            for (File file : files) {
                if (file.getName().equals(MAC_DS_FILE)) {
                    continue;
                }
                TableProviderChecker.isFileCheck(file.toPath());

                if (!TableProviderChecker.isValidDatabaseFileName(file.getName())) {
                    throw new WrappedIOException(TableProviderConstants.BAD_FILE_NAME + file.getName());
                }

                haveFiles = true;
                boolean emptyFile = true;

                try (DataInputStream in = new DataInputStream(new BufferedInputStream
                        (new FileInputStream(file)))) {
                    while (in.available() != 0) {
                        emptyFile = false;
                        DatabaseUtils.KeyValue pair = DatabaseUtils.readKeyValue(in);
                        if (!TableProviderChecker.correctFileForKey(pair.key, tableDirectory.toPath(), file.toPath())) {
                            throw new WrappedIOException("Wrong  key placement");
                        }
                        result.put(pair.key, pair.value);
                    }
                }
                catch (IOException e) {
                    throw new IOException(e.getMessage());
                }

                if (emptyFile) {
                    throw new WrappedIOException("Empty file " + file);
                }
            }

            if (!haveFiles) {
                throw new WrappedIOException(TableProviderConstants.EMPTY_DIRECTORY);
            }

        }
        return result;
    }

    public static void deleteTableFromDisk(File tableDirectory) throws IOException {
        File[] directories = tableDirectory.listFiles();

        if (directories == null) {
            return;
        }
        FileManager fm = new FileManager(tableDirectory.toPath());
        fm.deleteAllFilesInCurrentDirectory();
        tableDirectory.delete();

    }

    public static void rewriteFilesThatChanged(Path tablePath, Map<String, String> values, Set<Path> filesChanged)
            throws IOException {

        if (Files.notExists(tablePath)) {
            Files.createDirectory(tablePath);
        }

        for (Map.Entry<String, String> entry : values.entrySet()) {

            Path file = TableProviderUtils.createFileForKeyIfNotExists(entry.getKey(), tablePath);
            if (filesChanged.contains(file)) {

                try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream
                        (new FileOutputStream(file.toFile(), true)))) {

                    DatabaseUtils.writeKeyValue(new DatabaseUtils.KeyValue(entry.getKey(), entry.getValue()), out);
                }
                catch (IOException e) {
                    throw new IOException("Unable to write to file: " + e.getMessage());
                }
            }
        }
    }

    public static void writeTable(Map<Path, List<DatabaseUtils.KeyValue>> fileMap) throws IOException {
        for (Path file : fileMap.keySet()) {
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream
                    (new FileOutputStream(file.toFile(), true)))) {
                List<DatabaseUtils.KeyValue> entries = fileMap.get(file);
                for (int i = 0; i < entries.size(); ++i) {
                    DatabaseUtils.writeKeyValue(entries.get(i), out);
                }
            }
            catch (IOException e) {
                throw new IOException("Unable to write to file: " + e.getMessage());
            }
        }
    }

    public static List<Class<?>> readTableSignature(Path path) throws IllegalArgumentException, IOException {
        List<Class<?>> classes = new ArrayList<>();
        try (Scanner in = new Scanner(new BufferedInputStream
                (new FileInputStream(path.toFile())))) {
            while (in.hasNext()) {
                Class<?> aClass = Type.BY_SHORT_NAME.get(in.next().trim());
                if (aClass != null) {
                    classes.add(aClass);
                } else {
                    throw new IllegalArgumentException(UNEXPECTED_CLASS_IN_STORABLE);
                }
            }
        }
        catch (IOException e) {
            throw new IOException(e.fillInStackTrace());
        }
        return classes;
    }

    public static void writeTableSignature(Path path, List<Class<?>> classes) throws IOException {
        Path signatureFile = path.resolve(SIGNATURE_FILE);
        Files.createFile(signatureFile);

        try (PrintStream out = new PrintStream(new BufferedOutputStream
                (new FileOutputStream(signatureFile.toFile(), true)))) {
            boolean first_element = true;
            for (Class<?> aClass : classes) {
                if (!first_element) {
                    out.print(" ");
                } else {
                    first_element = false;
                }
                out.print(Type.BY_CLASS.get(aClass));
            }
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static String dbDirPropertyCheck() {
        String databaseLocation = System.getProperty(PROPERTY_DIRECTORY);
        if (databaseLocation == null) {
            throw new TimeToFinishException("Unknown database location");
        }
        return databaseLocation;
    }
}
