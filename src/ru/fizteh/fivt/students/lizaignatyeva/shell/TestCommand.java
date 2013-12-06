package ru.fizteh.fivt.students.lizaignatyeva.shell;

public class TestCommand extends Command {
    public void run(String[] args) {
        for (String c : args) {
            System.out.println(c);
        }
    }
}
