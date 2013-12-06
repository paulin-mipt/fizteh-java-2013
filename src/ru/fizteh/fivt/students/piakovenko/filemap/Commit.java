package ru.fizteh.fivt.students.piakovenko.filemap;


import ru.fizteh.fivt.students.piakovenko.shell.Commands;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 27.10.13
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
public class Commit implements Commands {
    private final String name = "commit";
    private GlobalFileMapState db = null;

    public Commit(GlobalFileMapState t) {
        db = t;
    }

    public String getName() {
        return name;
    }


    public void perform(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("Wrong number of arguments! Usage: commit");
        }
        if (!db.isValidTable()) {
            System.out.println("no table");
            return;
        }
        db.commit();
    }

}
