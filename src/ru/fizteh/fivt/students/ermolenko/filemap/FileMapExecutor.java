package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Executor;
import ru.fizteh.fivt.students.ermolenko.shell.Command;

public class FileMapExecutor extends Executor<FileMapState> {

    public FileMapExecutor() {

        list();
    }

    public void list() {

        Command put = new Put();
        mapOfCmd.put(put.getName(), put);
        Command get = new Get();
        mapOfCmd.put(get.getName(), get);
        Command remove = new Remove();
        mapOfCmd.put(remove.getName(), remove);
        Command<FileMapState> exit = new Exit();
        mapOfCmd.put(exit.getName(), exit);
    }
}
