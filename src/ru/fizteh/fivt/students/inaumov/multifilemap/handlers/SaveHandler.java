package ru.fizteh.fivt.students.inaumov.multifilemap.handlers;

import ru.fizteh.fivt.students.inaumov.filemap.builders.TableBuilder;
import ru.fizteh.fivt.students.inaumov.filemap.handlers.WriteHandler;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SaveHandler {
    public static final int BUCKET_NUM = 16;
    public static final int TABLES_IN_ONE_DIR = 16;

    public static void saveTable(TableBuilder builder) throws IOException {
        File tableDir = builder.getTableDir();
        ArrayList<Set<String>> keysToSave = new ArrayList<Set<String>>();
        boolean bucketIsEmpty;

        for (int bucketNumber = 0; bucketNumber < BUCKET_NUM; ++bucketNumber) {
            keysToSave.clear();
            for (int i = 0; i < TABLES_IN_ONE_DIR; ++i) {
                keysToSave.add(new HashSet<String>());
            }
            bucketIsEmpty = true;

            for (final String key: builder.getKeys()) {
                if (MultiFileMapUtils.getDirNumber(key) == bucketNumber) {
                    int fileNumber = MultiFileMapUtils.getFileNumber(key);
                    keysToSave.get(fileNumber).add(key);
                    bucketIsEmpty = false;
                }
            }

            String bucketName = bucketNumber + ".dir";
            File bucketDirectory = new File(tableDir, bucketName);

            if (bucketIsEmpty) {
                MultiFileMapUtils.deleteFile(bucketDirectory);
            }

            for (int fileN = 0; fileN < TABLES_IN_ONE_DIR; ++fileN) {
                String fileName = fileN + ".dat";
                File file = new File(bucketDirectory, fileName);

                if (keysToSave.get(fileN).isEmpty()) {
                    MultiFileMapUtils.deleteFile(file);
                    continue;
                }

                if (!bucketDirectory.exists()) {
                    bucketDirectory.mkdir();
                }

                WriteHandler.saveToFile(file.getAbsolutePath(), keysToSave.get(fileN), builder);
            }
        }
    }
}
