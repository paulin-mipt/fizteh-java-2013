package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.*;
import java.io.IOException;

public class DropCommand extends Command {
    public DropCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        MultiFileMap multiFile = status.getMultiFileMap();
        if (multiFile == null) {
            throw new IllegalStateException(getName() + ": Command do not get MultiFileMap");
        }
        if (!multiFile.containsTable(args[0])) {
            System.out.println(args[0] + " not exists");
        } else {
            multiFile.removeTable(args[0]);
            System.out.println("dropped");
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 1;
    }
}