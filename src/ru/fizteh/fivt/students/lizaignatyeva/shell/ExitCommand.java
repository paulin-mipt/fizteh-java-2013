package ru.fizteh.fivt.students.lizaignatyeva.shell;

public class ExitCommand extends Command {
    public ExitCommand() {
        name = "exit";
        argumentsAmount = 0;
    }

    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        System.exit(0);
    }
}
