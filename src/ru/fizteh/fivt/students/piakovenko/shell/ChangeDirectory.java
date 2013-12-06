package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class ChangeDirectory implements Commands {
    private final String name = "cd";
    private CurrentStatus currentStatus;

    public ChangeDirectory(CurrentStatus cs) {
        currentStatus = cs;
    }


    public String getName() {
        return name;
    }


    public void perform(String[] array) throws IOException {
        if (array.length != 2) {
            throw new IOException("Wrong arguments! Usage ~ cd <destination>");
        }
        File f;
        f = new File(array[1]);
        if (!f.isAbsolute()) {
            f = new File(currentStatus.getCurrentDirectory(), array[1]);
        }
        if (!f.exists()) {
            throw new IOException(f.getCanonicalPath() + " doesn't exist!");
        }
        currentStatus.changeCurrentDirectory(f.getCanonicalPath());
    }

}
