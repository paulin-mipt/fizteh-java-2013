package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.commands.ChangeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.CopyCommand;
import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.MakeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.MoveCommand;
import ru.fizteh.fivt.students.elenav.commands.PrintDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.PrintWorkingDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.RmCommand;


public class Shell {
    
    public static ShellState createShellState(String nm, File f, PrintStream ps) {
        ShellState shell = new ShellState(nm, f, ps);
        shell.addCommand(new ChangeDirectoryCommand(shell));
        shell.addCommand(new MakeDirectoryCommand(shell));
        shell.addCommand(new PrintWorkingDirectoryCommand(shell));
        shell.addCommand(new RmCommand(shell));
        shell.addCommand(new CopyCommand(shell));
        shell.addCommand(new MoveCommand(shell));
        shell.addCommand(new PrintDirectoryCommand(shell));
        shell.addCommand(new ExitCommand(shell));
        return shell;
    }
    
    public static void main(String[] args) throws IOException {
        ShellState shell = createShellState("my new state", new File("."), System.out);
        shell.run(args);
    }
}
