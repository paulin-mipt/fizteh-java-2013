package ru.fizteh.fivt.students.dsalnikov.shell;

public class ExitCommand implements Command {
    public String getName() {
        return "exit";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(Object shell, String[] st) {
        if (st.length != 1) {
            throw new IllegalArgumentException("Incorrect usage of Command exit: wrong amount of arguments");
        } else {
            System.exit(0);
        }
    }
}
