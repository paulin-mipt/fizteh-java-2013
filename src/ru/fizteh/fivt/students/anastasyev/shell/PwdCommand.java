package ru.fizteh.fivt.students.anastasyev.shell;

public class PwdCommand implements Command<Shell> {
    private static void pwd() {
        System.out.println(Shell.getUserDir().toPath().normalize());
    }

    @Override
    public final boolean exec(Shell state, final String[] command) {
        if (command.length != 1) {
            System.err.println("pwd: Usage - pwd");
            return false;
        }
        try {
            pwd();
        } catch (Exception e) {
            System.err.println("pwd: wrong path");
            return false;
        }
        return true;
    }

    @Override
    public final String commandName() {
        return "pwd";
    }
}
