package ru.fizteh.fivt.students.eltyshev.shell;

import ru.fizteh.fivt.students.eltyshev.shell.commands.*;

import java.io.IOException;

import java.util.ArrayList;

public class Program {
    public static void main(String[] Args) throws IOException {
        Shell shell = new Shell();

        ArrayList<Command<?>> commands = new ArrayList<Command<?>>();

        // putting MakeDirCommand
        Command<FileSystemShellState> command = new MakeDirCommand();
        commands.add(command);

        // putting DirCommand
        command = new DirCommand();
        commands.add(command);

        // putting CdCommand
        command = new CdCommand();
        commands.add(command);

        // putting PwdCommand
        command = new PwdCommand();
        commands.add(command);

        // putting RmCommand
        command = new RmCommand();
        commands.add(command);

        // putting MvCommand
        command = new MvCommand();
        commands.add(command);

        // putting ExitCommand
        command = new ExitCommand();
        commands.add(command);

        // putting CopyCommand
        command = new CopyCommand();
        commands.add(command);

        // putting HelpCommand
        command = new HelpCommand<FileSystemShellState>(commands);
        commands.add(command);

        shell.setCommands(commands);
        shell.setShellState(new FileSystemShellState());
        shell.setArgs(Args);
        shell.start();
    }
}
