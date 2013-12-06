package ru.fizteh.fivt.students.dubovpavel.filemap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;

public class DispatcherFileMap extends Dispatcher implements DataBaseAccessible<String, String> {
    private DataBaseWrapper dataBase;

    public DispatcherFileMap(boolean forwarding, String dbPath) {
        super(forwarding);
        try {
            dataBase = new DataBaseWrapper(getInitProperty("fizteh.db.dir"), this);
        } catch (DispatcherException e) {
            dataBase = null;
        }
    }

    public DataBaseHandler<String, String> getDataBase() {
        return dataBase;
    }
}
