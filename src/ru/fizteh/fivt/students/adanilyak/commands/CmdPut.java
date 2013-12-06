package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.filemap.FileMapGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:17
 */
public class CmdPut implements Cmd {
    private final String name = "put";
    private final int amArgs = 2;
    private FileMapGlobalState workState = null;

    public CmdPut(FileMapGlobalState dataBaseState) {
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
            String value = args.get(2);
            String result = workState.put(key, value);
            if (result == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(result);
            }
        } else {
            System.out.println("no table");
        }
    }
}
