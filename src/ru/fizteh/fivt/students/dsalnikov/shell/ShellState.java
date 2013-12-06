package ru.fizteh.fivt.students.dsalnikov.shell;


import java.io.File;

public class ShellState {
    String state;

   public ShellState() {
        File wd = new File("");
        wd.getAbsoluteFile();
        state = wd.getAbsolutePath();
    }

    public ShellState(String s) {
        state = s;
    }

    public String getState() {
        return state;
    }

    public String setState(String s) {
       return state = s;
    }

}
