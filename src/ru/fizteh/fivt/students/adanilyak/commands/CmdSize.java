package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 28.10.13
 * Time: 16:13
 */
public class CmdSize implements Cmd {
    private final String name = "size";
    private final int amArgs = 0;
    private MultiFileDataBaseGlobalState workState = null;

    public CmdSize(MultiFileDataBaseGlobalState dataBaseState) {
        workState = dataBaseState;
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
        if (workState.getCurrentTable() != null) {
            System.out.println(workState.size());
        } else {
            System.out.println("no table");
        }
    }
}
