package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyTableProviderFactory implements TableProviderFactory, AutoCloseable  {

    ClassState state = new ClassState(this);
    Map<String, TableProvider> providers = new HashMap<>();

    @Override
    public TableProvider create(String dir) throws IOException {
        state.check();

        if (providers.containsKey(dir) && !((DataBaseTable) providers.get(dir)).state.isClosed()) {
            return providers.get(dir);
        }

        if (dir == null || dir.trim().equals("")) {
            throw new IllegalArgumentException("Dir cannot be null");
        }

        File tableDirFile = new File(dir);

        if (!tableDirFile.exists()) {
            if (!tableDirFile.mkdirs()) {
                throw new IOException("Cannot create directory! " + tableDirFile.getCanonicalPath());
            }
        }

        if (!tableDirFile.isDirectory()) {
            throw new IllegalArgumentException("Wrong dir " + dir);
        }

        providers.put(dir, new DataBaseTable(dir));

        return providers.get(dir);
    }

    @Override
    public void close() throws Exception {
        if (state.isClosed()) {
            return;
        }

        state.close();
        for (TableProvider provider : providers.values()) {
            ((AutoCloseable) provider).close();
        }
        providers.clear();
    }
}
