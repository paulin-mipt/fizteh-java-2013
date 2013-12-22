package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;

public class StorageBuilder {
    String path;
    Dispatcher dispatcher;
    boolean pathIsProperty;
    DataBaseBuilder builder;

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setPath(boolean pathIsProperty, String path) {
        this.pathIsProperty = pathIsProperty;
        this.path = path;
    }

    public void setDataBaseBuilder(DataBaseBuilder dataBaseBuilder) {
        builder = dataBaseBuilder;
    }

    public Storage construct() {
        assert (path != null);
        assert (builder != null);
        if (dispatcher == null) {
            dispatcher = new Dispatcher(false);
            dispatcher.setQuiet(true);
        }
        if (pathIsProperty) {
            try {
                return new Storage(dispatcher.getInitProperty(path), dispatcher, builder);
            } catch (Dispatcher.DispatcherException e) {
                System.exit(-1);
                return null;
            }
        } else {
            return new Storage(path, dispatcher, builder);
        }
    }
}
