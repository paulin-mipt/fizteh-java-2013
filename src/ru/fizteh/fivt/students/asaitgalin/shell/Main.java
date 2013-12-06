package ru.fizteh.fivt.students.asaitgalin.shell;

import ru.fizteh.fivt.students.asaitgalin.shell.commands.*;

public class Main {
    public static void main(String[] args) {
        CommandTable table = new CommandTable();
        FilesystemController controller = new FilesystemController();
        ShellUtils shellUtils = new ShellUtils(table);

        table.appendCommand(new CdCommand(controller));
        table.appendCommand(new PwdCommand(controller));
        table.appendCommand(new DirCommand(controller));
        table.appendCommand(new MkdirCommand(controller));
        table.appendCommand(new RmCommand(controller));
        table.appendCommand(new CpCommand(controller));
        table.appendCommand(new MvCommand(controller));
        table.appendCommand(new ExitCommand());

        if (args.length > 0) {
            shellUtils.batchMode(args, System.err);
        } else {
            shellUtils.interactiveMode(System.in, System.out, System.err);
        }

    }

}
