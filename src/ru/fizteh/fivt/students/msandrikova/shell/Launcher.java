package ru.fizteh.fivt.students.msandrikova.shell;

public class Launcher {
    private static Command[] commands = new Command[] {
        new PrintWorkingDirectoryCommand(),
        new DescriptionOfDirectoryCommand(),
        new ChangeDirectoryCommand(),
        new MakeDirectoryCommand(),
        new RemoveFileOrDirectoryCommand(),
        new CopyFileOrDirectoryCommand(),
        new MoveFileOrDirectoryCommand(),
        new ExitCommand()
    };
    

    public static void main(String[] args) {
        Shell myShell = new Shell(commands, ".");
        myShell.execute(args);
    }

}
