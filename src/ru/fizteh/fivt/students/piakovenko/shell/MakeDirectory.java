package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 21:19
 * To change this template use File | Settings | File Templates.
 */
public class MakeDirectory implements Commands {
    private final String name = "mkdir";
    private CurrentStatus currentStatus;

    public MakeDirectory(CurrentStatus cs) {
        currentStatus = cs;
    }


    public String getName() {
        return name;
    }


    public void perform(String[] array) throws IOException {
        if (array.length != 2) {
            throw new IOException("Wrong arguments! Usage ~ mkdir <name of new directory>");
        }
        File f = new File(array[1]);
        if (!f.isAbsolute()) {
            f = new File(currentStatus.getCurrentDirectory(), array[1]);
        }
        if (!f.mkdirs()) {
            throw new IOException("Unable to create this directory - " + f.getCanonicalPath());
        }
    }
}
