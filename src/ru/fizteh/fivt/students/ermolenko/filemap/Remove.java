package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class Remove implements Command<FileMapState> {

    public String getName() {

        return "remove";
    }

    public void executeCmd(FileMapState inState, String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("incorrect number of arguments");
            return;
        }

        String value = inState.getDataBase().remove(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}