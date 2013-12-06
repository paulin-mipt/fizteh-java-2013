package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Commands;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 21.10.13
 * Time: 0:11
 * To change this template use File | Settings | File Templates.
 */
public class Use implements Commands {
    private final String name = "use";
    private GlobalFileMapState dbc = null;

    public Use(GlobalFileMapState t) {
        dbc = t;
    }

    public String getName() {
        return name;
    }

    public void perform(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IOException("Wrong number of arguments! Usage: use <keyValue>");
        }
        dbc.use(args[1]);
    }
}
