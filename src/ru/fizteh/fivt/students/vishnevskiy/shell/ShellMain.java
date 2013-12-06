package ru.fizteh.fivt.students.vishnevskiy.shell;

import java.util.ArrayList;

public class ShellMain {
    public static void main(String[] args) {
        Shell shell = new Shell(new ArrayList<Command>(), new State() {
        });
        shell.run(args);
    }
}
