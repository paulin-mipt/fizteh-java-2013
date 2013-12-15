package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CmdUse implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {

        return "use";
    }

    @Override
    public void executeCmd(MultiFileHashMapState inState, String[] args) throws IOException {

        if (inState.getCurrentTable() != null) {
            if (!inState.getCurrentTable().getName().equals(args[0])) {
                int size = inState.getChangesBaseSize();
                if (size != 0) {
                    System.out.println(size + " unsaved changes");
                    return;
                }
            } else {
                return;
            }
        }

        if (inState.getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        }

        Map<String, String> tmpDataBase = inState.getTable(args[0]).getDataBase();
        File tmpDataFile = inState.getTable(args[0]).getDataFile();

        MultiFileHashMapUtils.read(tmpDataFile, tmpDataBase);

        inState.setCurrentTable(args[0], tmpDataBase, tmpDataFile);

        System.out.println("using " + args[0]);
    }
}
