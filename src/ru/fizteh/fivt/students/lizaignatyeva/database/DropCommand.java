package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class DropCommand extends Command {
    public DropCommand() {
        name = "drop";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        String tableName = args[0];
        if (!DbMain.tableExists(tableName)) {
            System.out.println(tableName + " not exists");
            return;
        }
        Table table = DbMain.getTable(tableName);
        table.delete();
        DbMain.removeTable(tableName);
        DbMain.resetCurrentTable();
        System.out.println("dropped");
    }
}
