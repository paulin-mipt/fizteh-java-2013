package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CmdUse implements Command<StoreableState> {

    @Override
    public String getName() {

        return "use";
    }

    @Override
    public void executeCmd(StoreableState inState, String[] args) throws IOException {

        if (args.length != 1) {
            throw new IOException("incorrect number of arguments");
        }

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

        HashMap<String, Storeable> tmpDataBase = inState.getTable(args[0]).getDataBase();
        File tmpDataFile = inState.getTable(args[0]).getDataFile();
        List<Class<?>> tmpColumnOfTypes = inState.getTable(args[0]).getColumnOfTypes();

        StoreableUtils.read(tmpDataFile, inState.getTable(args[0]), tmpDataBase, inState.getProvider());

        inState.setCurrentTable(args[0], tmpColumnOfTypes, inState.getProvider(), tmpDataBase, tmpDataFile);

        System.out.println("using " + args[0]);
    }
}
