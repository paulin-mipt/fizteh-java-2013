package ru.fizteh.fivt.students.elenav.storeable.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.students.elenav.commands.AbstractCommand;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableState;
import ru.fizteh.fivt.students.elenav.storeable.TypeClass;

public class CreateTableCommand extends AbstractCommand {

    public CreateTableCommand(StoreableTableState s) {
        super(s, "create", -1);
    }

    public void execute(String[] args) throws IOException {
        if (args.length < 3) {
            throw new IllegalArgumentException("wrong type (too few args)");
        }
        FilesystemState result = getState().provider.createTable(args[1], identifyTypes(args));
        if (result != null) {
            getState().getStream().println("created");
        } else { 
            getState().getStream().println(args[1] + " exists");
        }
    }

    private List<Class<?>> identifyTypes(String[] args) throws IOException {
        List<Class<?>> result = new ArrayList<>();
        int lastIndex = args.length - 1;
        if (!args[2].startsWith("(") || !args[lastIndex].endsWith(")")) {
            throw new IOException("usage: create tablename (type1 type2 ... typeN)");
        }
        if (args[2].endsWith("(")) {
            args[2] = null;
        } else {
            args[2] = args[2].substring(1);
        }
        if (args[lastIndex].startsWith(")")) {
            args[lastIndex] = null;
        } else {
            args[lastIndex] = args[lastIndex].substring(0, args[lastIndex].length() - 1);
        }
        for (int i = 2; i < args.length; ++i) {
            String type = args[i];
            if (type != null) {
                result.add(TypeClass.getTypeWithName(type));
            }
        }
        return result;
    }
}
