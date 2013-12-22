
package ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils;

import ru.fizteh.fivt.students.dmitryKonturov.dataBase.DatabaseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MultiFileMapLoaderWriter {

    private static void loadDatabaseSubdirectory(Path subDir, Map<String, String> base) throws DatabaseException,
                IOException {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(subDir)) {
            for (Path entry : stream) {
                SimpleDatabaseLoaderWriter.databaseLoadFromFile(base, entry);
            }
        } catch (IOException e) {
            throw new IOException(subDir.toFile().getName() + " subdirectory", e);
        } catch (Exception exception) {
            throw new DatabaseException(subDir.toFile().getName(), exception);
        }
    }

    public static void loadDatabase(Path workspace, String baseName, Map<String, String> base) throws DatabaseException,
            IOException {
        Path baseDir;
        try {
            baseDir = workspace.resolve(baseName);
            CheckDatabasesWorkspace.checkDatabaseDirectory(baseDir);
        } catch (Exception e) {
            throw new DatabaseException("Load", e);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir)) {
            for (Path entry : stream) {
                if (!entry.toFile().getName().equals(StoreableUtils.getSignatureFileName())) {
                    loadDatabaseSubdirectory(entry, base);
                }
            }
        } catch (IOException e) {
          throw new IOException("Fail to load " + baseName + " table", e);
        } catch (Exception exception) {
            throw new DatabaseException("Fail to load " + baseName + " table", exception);
        }
    }

    private static void writeToSubdir(Path subdir, Map<String, String> base) throws DatabaseException,
        IOException {

        boolean toDelete = true;
        try {
            if (!Files.exists(subdir)) {
                Files.createDirectory(subdir);
            }
            Map<String, String>[] bases = new HashMap[16];
            for (int i = 0; i < 16; ++i) {
                bases[i] = new HashMap<>();
            }
            for (Map.Entry<String, String> entry : base.entrySet()) {
                int hash = Math.abs(entry.getKey().hashCode()) / 16;
                bases[hash % 16].put(entry.getKey(), entry.getValue());
            }
            for (int i = 0; i < 16; ++i) {
                Path file = subdir.resolve(String.format("%d.dat", i));
                SimpleDatabaseLoaderWriter.databaseWriteToFile(bases[i], file);
                if (Files.size(file) == 0) {
                    Files.delete(file);
                } else {
                    toDelete = false;
                }
            }
        } catch (IOException e) {
            throw new IOException(subdir.toFile().toString(), e);
        } catch (Exception e) {
            throw new DatabaseException(subdir.toFile().toString(), e);
        }
        if (toDelete) {
            try {
                Files.deleteIfExists(subdir);
            } catch (IOException e) {
                throw new IOException(subdir.toFile().toString(), e);
            }
        }
    }

    /*public static void writeDatabase(Path workspace, String baseName, Map<String, String> base)
            throws DatabaseException, IOException {

        try {
            Map<String, String>[] bases = new HashMap[16];
            for (int i = 0; i < 16; ++i) {
                bases[i] = new HashMap<>();
            }
            for (Map.Entry<String, String> entry : base.entrySet()) {
                int hash = Math.abs(entry.getKey().hashCode());
                //System.out.println(entry.getKey() + " <-:-> " + entry.getValue());
                bases[hash % 16].put(entry.getKey(), entry.getValue());
            }
            Path baseDir = workspace.resolve(baseName);
            for (int i = 0; i < 16; ++i) {
                writeToSubdir(baseDir.resolve(String.format("%d.dir", i)), bases[i]);
            }
        } catch (IOException e) {
            throw new IOException("Couldn't save " + baseName + " table", e);
        } catch (Exception e) {
            throw new DatabaseException("Couldn't save " + baseName + " table", e);
        }
    } */

    public static void writeMultipleDatabase(Path workspace, String baseName, Map<String, String>[] bases)
        throws DatabaseException, IOException {
        if (bases == null) {
            return;
        }
        try {
            Path baseDir = workspace.resolve(baseName);
            for (int i = 0; i < bases.length; ++i) {
                if (bases[i] != null) {
                    writeToSubdir(baseDir.resolve(String.format("%d.dir", i)), bases[i]);
                }
            }
        } catch (IOException e) {
            throw new IOException("Couldn't save " + baseName + " table", e);
        } catch (Exception e) {
            throw new DatabaseException("Couldn't save " + baseName + " table", e);
        }
    }

    public static void recursiveRemove(Path toRemove) throws IOException {
        try {
            File file = toRemove.toFile();
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    recursiveRemove(entry.toPath());
                }
            }
            if (!file.delete()) {
                throw new IOException("Cannot delete file: " + file.toString());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
