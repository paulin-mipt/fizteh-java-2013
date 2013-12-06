package ru.fizteh.fivt.students.adanilyak.commands;

import ru.fizteh.fivt.students.adanilyak.multifilehashmap.MultiFileDataBaseGlobalState;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 14:19
 */
public class CmdUse implements Cmd {
    private final String name = "use";
    private final int amArgs = 1;
    private MultiFileDataBaseGlobalState workState = null;

    public CmdUse(MultiFileDataBaseGlobalState dataBaseState) {
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
        String useTableName = args.get(1);
        if (!workState.isTableExist(useTableName)) {
            System.err.println(useTableName + " not exists");
        } else {
            if (workState.getCurrentTable() != null) {
                int amChanges = workState.amountOfChanges();
                if (amChanges != 0) {
                    System.err.println(amChanges + " unsaved changes");
                    return;
                }
            }
            workState.setCurrentTable(useTableName);
            System.out.println("using " + useTableName);
        }
    }
}
