package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.File;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MultiFileMapProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("can't create TableProvider: null directory");
        }
        File f = new File(dir);
        if (!f.isDirectory()) {
            throw new IllegalArgumentException("can't create TableProvider: it is not directory");
        }
        return new MultiFileMapProvider(f, System.out);
    }

}
