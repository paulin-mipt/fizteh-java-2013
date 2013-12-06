package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;

import java.io.IOException;


public class Put implements Commands {
    private final String name = "put";
    private GlobalFileMapState db = null;

    public Put(GlobalFileMapState db) {
        this.db = db;
    }

    public String getName() {
        return name;
    }

    public void perform(String[] args) throws IOException {
        if (!db.isValidTable()) {
            System.out.println("no table");
            return;
        }
        if (args.length != 3) {
            throw new IOException("Wrong number of arguments! Usage: get <keyValue>");
        }
        db.put(args[1], args[2]);
    }
}
