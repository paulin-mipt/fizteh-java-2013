package ru.fizteh.fivt.students.inaumov.shell.commands;

import ru.fizteh.fivt.students.inaumov.shell.ShellUtils;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.shell.ShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class DirCommand extends AbstractCommand<ShellState> {
    public DirCommand() {
        super("dir", 0);
    }

    public void execute(String argumentsLine, ShellState shellState) {
        String[] arguments = Shell.parseCommandParameters(argumentsLine);
        ShellUtils.checkArgumentsNumber(this, arguments.length);

        String[] dirContent = shellState.fileCommander.getCurrentDirectoryContent();
        for (final String entry: dirContent) {
            System.out.println(entry);
        }
    }
}
