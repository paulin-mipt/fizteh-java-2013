package ru.fizteh.fivt.students.drozdowsky.database;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.drozdowsky.utils.Utils;

import java.io.File;

public class MfhmProviderFactory implements TableProviderFactory {
    public MultiFileHashMap create(String dir) {
        if (!Utils.isValid(dir) || !(new File(dir).isDirectory())) {
            throw new IllegalArgumentException();
        }
        return new MultiFileHashMap(dir);
    }
}
