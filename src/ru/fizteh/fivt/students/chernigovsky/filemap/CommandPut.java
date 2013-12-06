package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.text.ParseException;

import java.io.IOException;

public class CommandPut implements Command {
    public String getName() {
        return "put";
    }
    public int getArgumentsCount() {
        return 2;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        if (state.currentTableIsNull()) {
            System.out.println("no table");
            return;
        }
        try {
            String oldValue = state.putToCurrentTable(args[1], args[2]);

            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(oldValue);
            }
        } catch (ParseException ex) {
            System.out.println("wrong type (description)");
        }
    }
}
