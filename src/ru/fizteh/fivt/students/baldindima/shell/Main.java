package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class Main {


    public static void main(final String[] args) throws IOException, ExitException {
        try {
            Shell shell = new Shell();
            FileFunctions fileFunctions = new FileFunctions();
            shell.addCommand(new ShellCd(fileFunctions));
            shell.addCommand(new ShellPwd(fileFunctions));
            shell.addCommand(new ShellMkdir(fileFunctions));
            shell.addCommand(new ShellRm(fileFunctions));
            shell.addCommand(new ShellMv(fileFunctions));
            shell.addCommand(new ShellCp(fileFunctions));
            shell.addCommand(new ShellDir(fileFunctions));
            shell.addCommand(new ShellExit());
            if (args.length > 0) {
                shell.nonInteractiveMode(args);
                //shell.interactiveMode();
            } else {
                shell.interactiveMode();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
}

