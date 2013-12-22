package ru.fizteh.fivt.students.dubovpavel.filemap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;

import java.io.File;

public class DataBaseWrapper extends DataBase {
    public DataBaseWrapper(String directory, DispatcherFileMap dispatcher) {
        super(new File(directory, "db.dat"), new StringSerial());
        try {
            open();
        } catch (DataBaseException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING, e.getMessage());
        }
    }
}
