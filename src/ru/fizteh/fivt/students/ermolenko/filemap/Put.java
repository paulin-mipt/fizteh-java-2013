package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class Put implements Command<FileMapState> {

    public String getName() {

        return "put";
    }

    public void executeCmd(FileMapState inState, String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("incorrect number of arguments");
            return;
        }

        String oldValue = inState.getDataBase().put(args[0], args[1]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
