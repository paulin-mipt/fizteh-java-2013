package ru.fizteh.fivt.students.dsalnikov.filemap;

import ru.fizteh.fivt.students.dsalnikov.shell.Command;

public class GetCommand implements Command {
    public String getName() {
        return "get";
    }

    public int getArgsCount() {
        return 1;
    }

    public void execute(Object f, String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Incorrect usage of Command get: wrong amount of arguments");
        } else {
            FileMapState filemap = (FileMapState)f;
            String temp = filemap.getValue(args[1]);
            if (temp == null) {
                System.out.println("not found " + args[1]);
                return;
            }
            System.out.println("found " + args[1] + " " + temp);
        }
    }
}
