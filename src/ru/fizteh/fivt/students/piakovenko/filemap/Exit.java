package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 11.10.13
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class Exit implements Commands {
    private final String name = "exit";
    private GlobalFileMapState db;

    public String getName() {
        return name;
    }

    public Exit(GlobalFileMapState dataBase) {
        db = dataBase;
    }

    public void perform(String[] s) throws IOException {
        if (s.length != 1) {
            throw new IOException("Wrong arguments! Usage ~ exit");
        }
        if (!db.isValidTable()) {
            System.exit(0);
        }
        try {
            db.saveTable();
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
