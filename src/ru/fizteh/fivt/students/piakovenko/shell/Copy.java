package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 21:54
 * To change this template use File | Settings | File Templates.
 */
public class Copy implements Commands {
    private final String name = "cp";
    private CurrentStatus currentStatus;

    Copy(CurrentStatus cs) {
        currentStatus = cs;
    }


    public String getName() {
        return name;
    }


    public void perform(String[] array) throws IOException {
        if (array.length != 3) {
            throw new IOException("Wrong arguments! Usage ~ cp <source> <destination>");
        }
        File from;
        File to;
        from = new File(array[1]);
        if (!from.isAbsolute()) {
            from = new File(currentStatus.getCurrentDirectory(), array[1]);
        }
        to = new File(array[2]);
        if (!to.isAbsolute()) {
            to = new File(currentStatus.getCurrentDirectory(), array[2]);
        }
        if (from.equals(to)) {
            throw new IOException("Trying to copy file to the same file");
        }
        if (!to.exists()) {
            if (to.getName().indexOf('.') == -1) {
                to.mkdirs();
            } else {
                to.createNewFile();
            }
        }
        if (from.isFile() && to.isFile()) {
            CopyFiles.copy(from, to);
        } else if (from.isFile() && to.isDirectory()) {
            File fromNew = new File(to.getCanonicalPath() + File.separator + from.getName());
            fromNew.createNewFile();
            CopyFiles.copy(from, fromNew);
        } else if (from.isDirectory()) {
            CopyFiles.copyRecursively(from, to);
        } else {
            throw new IOException("Error! " + array[1] + " should be a directory!");
        }
    }
}
