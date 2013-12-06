package ru.fizteh.fivt.students.piakovenko.filemap;


import ru.fizteh.fivt.students.piakovenko.shell.Commands;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class Get implements Commands {
    private final String name = "get";
    private GlobalFileMapState db;

    public Get(GlobalFileMapState dataBase) {
        db = dataBase;
    }

    public String getName() {
        return name;
    }

    public void perform(String[] args) throws IOException {
        if (!db.isValidTable()) {
            System.out.println("no table");
            return;
        }
        if (args.length != 2) {
            throw new IOException("Wrong number of arguments! Usage: get <keyValue>");
        }
        db.get(args[1]);
    }
}
