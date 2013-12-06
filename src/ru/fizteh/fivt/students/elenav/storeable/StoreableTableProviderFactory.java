package ru.fizteh.fivt.students.elenav.storeable;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class StoreableTableProviderFactory implements TableProviderFactory {
    
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("can't create TableProvider: not existing directory");
        }
        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new IOException("can't create TableProvider: unknown error");
            } 
        } else {
            if (!f.isDirectory()) {
                throw new IllegalArgumentException("can't create TableProvider: it is not directory");
            }
        }
        return new StoreableTableProvider(f, System.out);
    }

}
