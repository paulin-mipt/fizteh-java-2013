package ru.fizteh.fivt.students.mikhaylova_daria.db;

import ru.fizteh.fivt.storage.structured.*;

import java.io.IOException;

public class TableManagerFactory implements TableProviderFactory {

    public TableManagerFactory() {}

    public TableManager create(String dir) throws IllegalArgumentException, IOException {
        if (dir == null) {
            throw new IllegalArgumentException("wrong type (Argument is null)");
        }
        return new TableManager(dir);
    }

}
