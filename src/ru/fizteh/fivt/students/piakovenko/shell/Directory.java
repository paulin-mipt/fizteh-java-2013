package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class Directory implements Commands {
    private final String name = "dir";
    private CurrentStatus currentStatus;

    public Directory(CurrentStatus cs) {
        currentStatus = cs;
    }

    public void changeCurrentStatus(Object obj) {
        currentStatus = (CurrentStatus) obj;
    }

    public String getName() {
        return name;
    }


    public void perform(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("Wrong arguments! Usage ~ dir");
        }
        if (!currentStatus.getCurrentFile().isDirectory()) {
            throw new IOException("Error! " + currentStatus.getCurrentDirectory() + " is not a directory!");
        }
        for (String s : currentStatus.getCurrentFile().list()) {
            System.out.println(s);
        }
    }
}
