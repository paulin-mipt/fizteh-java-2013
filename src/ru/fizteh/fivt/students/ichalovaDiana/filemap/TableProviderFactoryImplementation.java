package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TableProviderFactoryImplementation implements TableProviderFactory, AutoCloseable {
    
    private Set<TableProviderImplementation> tableProviders = new HashSet<TableProviderImplementation>();
    
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock writeLock = readWriteLock.writeLock();
    
    private volatile boolean isClosed = false;
    
    public TableProviderFactoryImplementation() {}

    @Override
    public TableProvider create(String dir) throws IOException {
        isClosed();
        
        if (!isValidDatabaseDirectory(dir)) {
            throw new IllegalArgumentException("Invalid dir path");
        }
         
        Path dbDir = Paths.get(dir);
        
        if (!Files.exists(dbDir)) {
            Files.createDirectories(dbDir);
        }
        
        if (!Files.isDirectory(dbDir)) {
            throw new IllegalArgumentException(dbDir + " is not a directory");
        }
        
        isCorrectDatabaseDirectory(dbDir);
        
        TableProviderImplementation database = new TableProviderImplementation(dbDir);
        
        tableProviders.add(database);
        
        return database;
    }

    private static void isCorrectDatabaseDirectory(Path databaseDirectory) throws IllegalArgumentException {        
        for (String dirName : databaseDirectory.toFile().list()) {         
            if (Files.isDirectory(databaseDirectory.resolve(dirName))) {
                isCorrectTableDirectory(databaseDirectory.resolve(dirName));
            } else {
                throw new IllegalArgumentException("Invalid table format");
            }
        }
    }
    
    private static boolean isValidDatabaseDirectory(String dir) {
        if (dir == null || dir.isEmpty() || dir.matches(".*\\s+.*")) {
            return false;
        }
        return true;
    }
    
    private static void isCorrectTableDirectory(Path tableDirectory) throws IllegalArgumentException {
        if (tableDirectory.toFile().list().length == 0) {
            throw new IllegalArgumentException("Invalid table format");
        }
        
        boolean containsSignatureFile = false;
        for (String dirName : tableDirectory.toFile().list()) {
            if (dirName.equals("signature.tsv") && Files.isRegularFile(tableDirectory.resolve(dirName))) {
                containsSignatureFile = true;
                continue;
            }
            
            if (tableDirectory.resolve(dirName).toFile().list().length == 0) {
                throw new IllegalArgumentException("Invalid table format");
            }
            
            if (!dirName.matches("(1[0-5]|[0-9]).dir")) {
                throw new IllegalArgumentException("Invalid table format");
            }
            Path fileDirectory = tableDirectory.resolve(dirName);
            if (!Files.isDirectory(fileDirectory)) {
                throw new IllegalArgumentException("Invalid table format");
            }
            for (String fileName : fileDirectory.toFile().list()) {
                if (!fileName.matches("(1[0-5]|[0-9]).dat")) {
                    throw new IllegalArgumentException("Invalid table format");
                }
                if (!Files.isRegularFile(fileDirectory.resolve(fileName))) {
                    throw new IllegalArgumentException("Invalid table format");
                }
                isCorrectTableFile(fileDirectory.resolve(fileName));
            }
        }  

        if (!containsSignatureFile) {
            throw new IllegalArgumentException("Invalid table format");
        }
    }
    
    private static void isCorrectTableFile(Path tableFile) throws IllegalArgumentException {
        if (tableFile.toFile().length() == 0) {
            throw new IllegalArgumentException("Invalid table format");
        }
        int nDirectory = Integer.parseInt(tableFile.getParent().getFileName().toString().replaceFirst("[.][^.]+$", ""));
        int nFile = Integer.parseInt(tableFile.getFileName().toString().replaceFirst("[.][^.]+$", ""));
        try (FileDatabase currentDatabase = new FileDatabase(tableFile)) {
            currentDatabase.selfCheck(nDirectory, nFile);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while reading from file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }

    @Override
    public void close() throws Exception {
        if (!isClosed) {
            writeLock.lock();
            try {
                if (!isClosed) {
                    for (TableProviderImplementation tableProvider : tableProviders) {
                        tableProvider.close();
                    }
                    isClosed = true;
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    private void isClosed() {
        if (isClosed) {
            throw new IllegalStateException("TableProviderFactory object is closed");
        }
    }
}
