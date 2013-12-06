package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class UseCommand extends Command {
    public UseCommand() {
        name = "use";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        DbMain.saveCurrentTable();
        String tableName = args[0];
        if (!DbMain.tableExists(tableName)) {
            System.out.println(tableName + " not exists");
            return;
        }
        DbMain.setCurrentTable(tableName);
        System.out.println("using " + tableName);

    }
}
