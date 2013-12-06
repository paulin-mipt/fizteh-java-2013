package ru.fizteh.fivt.students.anastasyev.shell;

public class ExitCommand implements Command<Shell> {
    public final boolean exec(Shell state, final String[] command) {
        if (command.length != 1) {
            System.err.println("exit: Usage - exit");
            System.exit(1);
            return false;
        }
        System.exit(0);
        return true;
    }

    public final String commandName() {
        return "exit";
    }
}
