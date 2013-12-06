package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.filemap.FileMapGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:18
 */
public class CmdRemove implements Cmd {
    private final String name = "remove";
    private final int amArgs = 1;
    private FileMapGlobalState workState = null;

    public CmdRemove(FileMapGlobalState dataBaseState) {
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
            String key = args.get(1);
            String result = workState.remove(key);
            if (result == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } else {
            System.out.println("no table");
        }
    }
}
