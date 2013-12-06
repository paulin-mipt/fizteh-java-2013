package ru.fizteh.fivt.students.dsalnikov.filemap;

import ru.fizteh.fivt.students.dsalnikov.shell.Command;

public class RemoveCommand implements Command {
    public String getName() {
        return "remove";
    }

    public int getArgsCount() {
        return 1;
    }

    public void execute(Object f, String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("wrong usage of command remove: Wrong amount of arguments");
        } else {
            FileMapState filemap = (FileMapState)f;
            String temp = filemap.deleteValue(args[1]);
            if (temp == null) {
                System.out.println("not found");
                return;
            } else {
                System.out.println("removed");
            }
        }
    }
}
