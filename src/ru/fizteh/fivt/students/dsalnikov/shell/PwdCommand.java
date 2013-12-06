package ru.fizteh.fivt.students.dsalnikov.shell;

public class PwdCommand implements Command {

    public String getName() {
        return "pwd";
    }

    public int getArgsCount() {
        return 0;
    }

    public void execute(Object shell, String[] emptyStr) {
        if (emptyStr.length != 1) {
            throw new IllegalArgumentException("Incorrect usage of command pwd: wrong amount of arguments");
        } else {
            System.out.println(((ShellState)shell).getState());
        }
    }
}
