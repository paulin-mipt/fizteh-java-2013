package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.SimpleTableBuilder;
import ru.fizteh.fivt.students.eltyshev.filemap.base.StringTable;

import java.io.File;

public class DistributedTableBuilder extends SimpleTableBuilder {
    private int currentBucket;
    private int currentFile;

    public DistributedTableBuilder(StringTable table) {
        super(table);
    }

    @Override
    public void setCurrentFile(File curFile) {
        currentBucket = MultifileMapUtils.parseCurrentBucketNumber(curFile.getParentFile());
        currentFile = MultifileMapUtils.parseCurrentFileNumber(curFile);
    }

    @Override
    public void put(String key, String value) {
        MultifileMapUtils.checkKeyPlacement(key, currentBucket, currentFile);
        super.put(key, value);
    }
}
