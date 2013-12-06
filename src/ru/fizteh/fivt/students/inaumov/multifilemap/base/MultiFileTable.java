package ru.fizteh.fivt.students.inaumov.multifilemap.base;

import ru.fizteh.fivt.students.inaumov.filemap.base.AbstractTable;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import ru.fizteh.fivt.students.inaumov.filemap.handlers.*;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapUtils;

public class MultiFileTable extends AbstractTable {
    private static final int BUCKET_NUM = 16;
    private static final int TABLES_IN_ONE_DIR = 16;

    public MultiFileTable(String dir, String tableName) throws IOException, IllegalArgumentException {
        super(dir, tableName);
    }

    public void loadTable() throws IOException {
        File tableDir = getTableDir();
        //System.out.println("table directory = " + tableDir.getAbsolutePath());
        for (final File bucket: tableDir.listFiles()) {
            for (final File file: bucket.listFiles()) {
                ReadHandler.loadFromFile(file.getAbsolutePath(), tableHash);
            }
        }
    }

    public void saveTable() throws IOException {
        File tableDir = getTableDir();
        ArrayList< Set<String> > keysToSave = new ArrayList< Set<String> >();
        boolean bucketIsEmpty;

        for (int bucketNumber = 0; bucketNumber < BUCKET_NUM; ++bucketNumber) {
            keysToSave.clear();
            for (int i = 0; i < TABLES_IN_ONE_DIR; ++i) {
                keysToSave.add(new HashSet<String>());
            }
            bucketIsEmpty = true;

            for (final String key: tableHash.keySet()) {
                if (getDirNumber(key) == bucketNumber) {
                    int fileNumber = getFileNumber(key);
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

                WriteHandler.saveToFile(file.getAbsolutePath(), keysToSave.get(fileN), tableHash);
            }
        }
    }

    private File getTableDir() {
        File tableDir = new File(getDir(), getName());
        if ( !tableDir.exists() ) {
            tableDir.mkdir();
        }

        return tableDir;
    }

    private int getDirNumber(String key) {
        byte[] bytes = key.getBytes(CHARSET);
        int firstSymbol = Math.abs(bytes[0]);

        return firstSymbol % BUCKET_NUM;
    }

    private int getFileNumber(String key) {
        byte[] bytes = key.getBytes(CHARSET);
        int firstSymbol = Math.abs(bytes[0]);

        return firstSymbol / BUCKET_NUM % TABLES_IN_ONE_DIR;
    }
}
