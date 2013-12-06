package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class ExitCommand extends Command {
    public ExitCommand() {
        name = "exit";
        argumentsAmount = 0;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        //DbMain.getCurrentDatabase().write();

        try {
            DbMain.saveCurrentTable();
        } catch (Exception e) {
            System.err.println("Ooops! Error: " + e.getMessage());
        }
        System.exit(0);
    }
}
