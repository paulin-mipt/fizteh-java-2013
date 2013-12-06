package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 19:41
 * To change this template use File | Settings | File Templates.
 */
public class CurrentStatus {
    private File currentDirectory;

    public CurrentStatus(File f) {
        currentDirectory = f;
    }

    public String getCurrentDirectory() throws IOException {
        return currentDirectory.getCanonicalPath();
    }


    public File getCurrentFile() {
        return currentDirectory;
    }

    public void changeCurrentDirectory(String s) throws IOException {
        currentDirectory = new File(s);
    }

}
