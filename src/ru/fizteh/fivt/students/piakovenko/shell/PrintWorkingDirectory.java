package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
public class PrintWorkingDirectory implements Commands {
    private final String name = "pwd";
    private CurrentStatus currentStatus;

    public PrintWorkingDirectory(CurrentStatus cs) {
        currentStatus = cs;
    }

    public String getName() {
        return name;
    }


    public void perform(String[] s) throws IOException {
        if (s.length != 1) {
            throw new IOException("Wrong arguments! Usage ~ pwd");
        }
        System.out.println(currentStatus.getCurrentDirectory());
    }
}
