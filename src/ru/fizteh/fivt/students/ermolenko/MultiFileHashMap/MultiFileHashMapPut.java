package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class MultiFileHashMapPut implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {

        return "put";
    }

    @Override
    public void executeCmd(MultiFileHashMapState inState, String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("incorrect number of arguments");
            return;
        }
        if (inState.getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        String value = inState.putToCurrentTable(args[0], args[1]);
        if (null == value) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
    }
}

