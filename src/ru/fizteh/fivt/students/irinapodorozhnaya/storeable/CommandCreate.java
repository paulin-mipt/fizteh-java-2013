package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Types;

public class CommandCreate extends AbstractCommand {
    
    private StoreableState state;
    
    public CommandCreate(StoreableState state) {
        super(-1);
        this.state = state;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void execute(String[] args) throws IOException {
        try {
            state.create(args[1], parser(args));
        } catch (IllegalArgumentException e) {
            throw new IOException("wrong type (" + e.getMessage() + ")");
        }
        state.getOutputStream().println("created");
    }
    
    public static List<Class<?>> parser(String[] args) throws IOException {
        if (args.length < 3) {
            throw new IOException("create: too few arguments");
        }

        List<Class<?>> columnType = new ArrayList<>();

        String first = args[2];
        String last = args[args.length - 1];

        if (!first.startsWith("(") || !last.endsWith(")")) {
            throw new IOException("create: Input not matchs \"create tablename (type1 ... typeN)\"");
        }
        if (args.length == 3) {
            columnType.add(Types.getTypeByName(first.substring(1, first.length() - 1)));
            return columnType;
        }

        first = first.substring(1);
        last = last.substring(0, last.length() - 1);

        columnType.add(Types.getTypeByName(first));
        for (int i = 3; i < args.length - 1; ++i) {
            columnType.add(Types.getTypeByName(args[i]));
        }
        columnType.add(Types.getTypeByName(last));
        return columnType;
    }
}
