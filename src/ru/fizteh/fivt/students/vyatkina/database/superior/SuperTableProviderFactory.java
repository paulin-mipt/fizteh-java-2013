package ru.fizteh.fivt.students.vyatkina.database.superior;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SuperTableProviderFactory {

    public static final String NULL_DIRECTORY = "Directory is null";
    public static final String DATABASE_IS_NOT_A_DIRECTORY = "Database is not a directory";

    public static final String EMPTY_DIRECTORY = "Directory is empty";

    public static Path directoryCheck(String dir) throws IOException {
        if (dir == null) {
            throw new IllegalArgumentException(NULL_DIRECTORY);
        }

        if (dir.trim().isEmpty()) {
            throw new IllegalArgumentException(EMPTY_DIRECTORY);
        }

        Path directory = Paths.get(dir);

        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
        }

        if (directory.toFile().isFile()) {
            throw new IllegalArgumentException(DATABASE_IS_NOT_A_DIRECTORY);
        }

        if (!Files.isDirectory(directory)) {
            throw new IOException(DATABASE_IS_NOT_A_DIRECTORY);
        }

        return directory;
    }

}
