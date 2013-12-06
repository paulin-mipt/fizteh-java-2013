package ru.fizteh.fivt.students.piakovenko.filemap;


import ru.fizteh.fivt.students.piakovenko.shell.Commands;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public class Remove implements Commands {
    private final String name = "remove";
    private GlobalFileMapState db;

    public Remove(GlobalFileMapState dataBase) {
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
        db.removeKey(args[1]);
    }
}
