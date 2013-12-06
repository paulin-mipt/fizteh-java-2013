package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) {
        Shell shell = new Shell();
        ShellState state = new ShellState();
        shell.addCommand(new CdCommand(state));
        shell.addCommand(new CpCommand(state));
        shell.addCommand(new DirCommand(state));
        shell.addCommand(new ExitCommand());
        shell.addCommand(new MkdirCommand(state));
        shell.addCommand(new MvCommand(state));
        shell.addCommand(new PwdCommand(state));
        shell.addCommand(new RmCommand(state));
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            String arguments = StringMethods.join(Arrays.asList(args), " ");
            try {
                shell.batchMode(arguments);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

    }
}

