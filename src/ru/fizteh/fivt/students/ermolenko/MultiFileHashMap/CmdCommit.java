package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class CmdCommit implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {

        return "commit";
    }

    @Override
    public void executeCmd(MultiFileHashMapState inState, String[] args) throws IOException {

        System.out.println(inState.getCurrentTable().commit());
    }
}
