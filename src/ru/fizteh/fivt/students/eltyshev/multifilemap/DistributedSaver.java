package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapWriter;
import ru.fizteh.fivt.students.eltyshev.filemap.base.TableBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DistributedSaver {
    static final int BUCKET_COUNT = 16;
    static final int FILES_PER_DIR = 16;

    public static void save(TableBuilder builder, Set<DatabaseFileDescriptor> changedFiles) throws IOException {
        File tableDirectory = builder.getTableDirectory();

        for (DatabaseFileDescriptor descriptor : changedFiles) {
            Set<String> keysToSave = getKeysToSave(builder, descriptor);
            String bucketName = String.format("%d.dir", descriptor.bucket);
            String fileName = String.format("%d.dat", descriptor.file);
            File bucketDirectory = new File(tableDirectory, bucketName);
            File file = new File(bucketDirectory, fileName);
            if (keysToSave.isEmpty()) {
                MultifileMapUtils.deleteFile(file);
            } else {
                if (!bucketDirectory.exists()) {
                    bucketDirectory.mkdir();
                }
                FilemapWriter.saveToFile(file.getAbsolutePath(), keysToSave, builder);
            }
        }
        cleanTableDirectory(builder);
    }

    private static Set<String> getKeysToSave(TableBuilder builder, DatabaseFileDescriptor descriptor) {
        HashSet<String> result = new HashSet<>();
        for (final String key : builder.getKeys()) {
            DatabaseFileDescriptor tempDescriptor = MultifileMapUtils.makeDescriptor(key);
            if (descriptor.equals(tempDescriptor)) {
                result.add(key);
            }
        }
        return result;
    }

    private static void cleanTableDirectory(TableBuilder builder) {
        File tableDirectory = builder.getTableDirectory();
        File[] buckets = tableDirectory.listFiles();
        if (buckets.length == 0) {
            return;
        }
        for (File bucket : buckets) {
            if (bucket.isFile()) {
                continue;
            }
            File[] files = bucket.listFiles();
            if (files == null || files.length == 0) {

                MultifileMapUtils.deleteFile(bucket);
            }
        }
    }
}
