package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class GetCommand extends Command {
    public GetCommand() {
        name = "get";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        Table table = DbMain.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            return;
        }
        String key = args[0];
        String value = table.data.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
