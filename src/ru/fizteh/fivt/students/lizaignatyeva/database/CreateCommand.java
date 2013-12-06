package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class CreateCommand extends Command {
    public CreateCommand() {
        name = "create";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        String tableName = args[0];
        if (DbMain.tableExists(tableName)) {
            System.out.println(tableName + " exists");
            return;
        }
        Table table = new Table(DbMain.directory, tableName);

        DbMain.addTable(table);
        System.out.println("created");
    }
}
