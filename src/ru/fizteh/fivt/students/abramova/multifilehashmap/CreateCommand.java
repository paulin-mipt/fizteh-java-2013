package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.shell.Command;
import ru.fizteh.fivt.students.abramova.shell.Status;
import java.io.IOException;

public class CreateCommand extends Command {
    public CreateCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        MultiFileMap multiFile = status.getMultiFileMap();
        if (multiFile == null) {
            throw new IllegalStateException(getName() + ": Command do not get MultiFileMap");
        }
        if (multiFile.containsTable(args[0])) {
            System.out.println(args[0] + " exists");
        } else {
            multiFile.addTable(args[0]);
            System.out.println("created");
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 1;
    }
}
