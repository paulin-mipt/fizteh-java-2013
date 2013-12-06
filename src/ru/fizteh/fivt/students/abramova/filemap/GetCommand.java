package ru.fizteh.fivt.students.abramova.filemap;

import java.util.Map;
import ru.fizteh.fivt.students.abramova.shell.Command;
import ru.fizteh.fivt.students.abramova.shell.Status;

public class GetCommand extends Command{
    public GetCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) {
        Map<String, String> fileMap = status.getFileMap().getMap();
        if (fileMap == null) {
            throw new IllegalStateException(getName() + ": Command do not get file");
        }
        String value = fileMap.get(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + value);
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 1;
    }
}
