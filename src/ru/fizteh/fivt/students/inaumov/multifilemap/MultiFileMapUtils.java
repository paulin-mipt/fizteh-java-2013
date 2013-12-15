package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.filemap.base.AbstractDatabaseTable;
import ru.fizteh.fivt.students.inaumov.multifilemap.handlers.SaveHandler;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiFileMapUtils {
    private static final Pattern DIR_PATTERN = Pattern.compile("([^\\.]+).dir");
    private static final Pattern FILE_PATTERN = Pattern.compile("([^\\.]+).dat");

    public static boolean isCorrectDir(String dir) {
        if (dir == null) {
            return false;
        }

        File file = new File(dir);
        if (file.isFile()) {
            return false;
        }

        return true;
    }

    public static void deleteFile(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }
        if (fileToDelete.isDirectory()) {
            for (final File file: fileToDelete.listFiles()) {
                deleteFile(file);
            }
        }
        fileToDelete.delete();
    }

    public static int getDirNumber(String key) {
        byte[] bytes = key.getBytes(AbstractDatabaseTable.CHARSET);
        int firstSymbol = Math.abs(bytes[0]);

        return firstSymbol % SaveHandler.BUCKET_NUM;
    }

    public static int getFileNumber(String key) {
        byte[] bytes = key.getBytes(AbstractDatabaseTable.CHARSET);
        int firstSymbol = Math.abs(bytes[0]);

        return firstSymbol / SaveHandler.BUCKET_NUM % SaveHandler.TABLES_IN_ONE_DIR;
    }

    public static int parseCurrentBucketNumber(File bucket) {
        String bucketName = bucket.getName();
        Matcher matcher = DIR_PATTERN.matcher(bucketName);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }

        throw new IllegalArgumentException("incorrect bucket name");
    }

    public static int parseCurrentFileNumber(File file) {
        String fileName = file.getName();
        Matcher matcher = FILE_PATTERN.matcher(fileName);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }

        throw new IllegalArgumentException("incorrect file name");
    }

    public static void checkKeyPlacement(String key, int currentBucket, int currentFile) {
        if (currentBucket != getDirNumber(key) || currentFile != getFileNumber(key)) {
            throw new IllegalArgumentException("incorrect key placement");
        }
    }
}
