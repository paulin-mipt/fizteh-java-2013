package ru.fizteh.fivt.students.abramova.filemap;

import java.util.Map;
import ru.fizteh.fivt.students.abramova.shell.Command;
import ru.fizteh.fivt.students.abramova.shell.Status;

public class PutCommand extends Command{
    public PutCommand(String name) {
        super(name);
    }
    @Override
    public int doCommand(String[] args, Status status) {
        Map<String, String> fileMap = status.getFileMap().getMap();
        if (fileMap == null) {
            throw new IllegalStateException(getName() + ": Command do not get file");
        }
        String oldValue = fileMap.get(args[0]);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + oldValue);
        }
        fileMap.put(args[0], args[1]);
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 2;
    }
}
