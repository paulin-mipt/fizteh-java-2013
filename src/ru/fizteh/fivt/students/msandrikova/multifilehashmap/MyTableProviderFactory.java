package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;

import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class MyTableProviderFactory implements ChangesCountingTableProviderFactory {
    @Override
    public ChangesCountingTableProvider create(String dir) throws IllegalArgumentException {
        if (Utils.isEmpty(dir)) {
            throw new IllegalArgumentException("Directory can not be null.");
        }
        ChangesCountingTableProvider newTableProvider = null;
        try {
            newTableProvider = new MyTableProvider(new File(dir));
        } catch (IllegalArgumentException e) {
            throw e;
        }
        return newTableProvider;
    }

}
