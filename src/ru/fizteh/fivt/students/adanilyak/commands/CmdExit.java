package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.filemap.FileMapGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 0:27
 */
public class CmdExit implements Cmd {
    private final String name = "exit";
    private final int amArgs = 0;
    private FileMapGlobalState workState = null;

    public CmdExit(FileMapGlobalState stateTable) {
        workState = stateTable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAmArgs() {
        return amArgs;
    }

    @Override
    public void work(List<String> args) throws IOException {
        /*
        if (workState.autoCommitOnExit) {
            if (workState.getCurrentTable() != null) {
                workState.commit();
            }
        }
        */
        System.exit(0);
    }
}
