package ru.fizteh.fivt.students.eltyshev.shell.commands;

public class ExitCommand<State> extends AbstractCommand<State> {

    public ExitCommand() {
        super("exit", "exit");
    }

    public void executeCommand(String params, State shellState) {
        System.out.println("Good bye!");
        System.exit(0);
    }
}
