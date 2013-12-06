package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CommandStoreableCreate implements Command {
    public String getName() {
        return "create";
    }
    public int getArgumentsCount() {
        return 2;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        args[2] = args[2].trim();
        if (!args[2].startsWith("(") || !args[2].endsWith(")")) {
            System.out.println("wrong argument");
        }
        if (state.createStoreableTable(args[1], args[2])) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }
}
