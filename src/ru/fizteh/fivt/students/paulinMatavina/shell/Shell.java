package ru.fizteh.fivt.students.paulinMatavina.shell;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class Shell {
    public static void main(String[] args) {
        ShellState state = new ShellState(); 
        try {
            CommandRunner.run(args, state);
        } catch (Exception e) {
            return;
        }
    }
}
