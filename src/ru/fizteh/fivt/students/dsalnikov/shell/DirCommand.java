package ru.fizteh.fivt.students.dsalnikov.shell;

import java.io.File;
import java.io.IOException;


public class DirCommand implements Command {
    public String getName() {
        return "dir";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(Object shell, String[] s) throws IOException {
        if (s.length != 1) {
            throw new IllegalArgumentException("Incorrect usage of Command dir: wrong amount of arguments");
        } else {
            ShellState sh = (ShellState)shell;
            File dir = new File(sh.getState());
            File[] arr = dir.listFiles();
            for (File f : arr) {
                if (f.isDirectory()) {
                    System.out.println(f.getName() + " <DIR>");
                }
            }
            for (File f : arr) {
                if (!f.isDirectory()) {
                    System.out.println(f.getName());
                }
            }
        }
    }

}
