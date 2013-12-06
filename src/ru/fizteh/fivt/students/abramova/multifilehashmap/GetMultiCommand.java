package ru.fizteh.fivt.students.abramova.multifilehashmap;

import ru.fizteh.fivt.students.abramova.filemap.FileMap;
import ru.fizteh.fivt.students.abramova.filemap.GetCommand;
import ru.fizteh.fivt.students.abramova.shell.Command;
import ru.fizteh.fivt.students.abramova.shell.Status;

import java.io.IOException;

public class GetMultiCommand extends Command {
    public GetMultiCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        if (!status.isMultiFileMap()) {
            throw new IllegalStateException(getName() + ": Command do not get MultiFileMap");
        }
        TableMultiFile table = status.getMultiFileMap().getWorkingTable();
        if (table == null) {
            System.out.println("no table");
        } else {
            FileMap file = table.findFileMap(args[0]);
            if (file != null) {
                new GetCommand(name).doCommand(args, new Status(file));
            } else {
                System.out.println("not found");
            }
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 1;
    }
}
