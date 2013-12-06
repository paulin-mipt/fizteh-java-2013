package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.students.valentinbarishev.shell.CommandString;
import ru.fizteh.fivt.students.valentinbarishev.shell.InvalidCommandException;
import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;

import java.io.IOException;

public class ShellCreateTable extends SimpleShellCommand {
    private Context context;

    public ShellCreateTable(Context newContext) {
        context = newContext;
        setName("create");
        setNumberOfArgs(3);
        setHint("usage: create <table name> (<type1 type2 type3 ...>)");
    }

    public void run() {
        try {
            if (context.provider.createTable(getArg(1), MySignature.getTypes(getSpacedArg(2))) != null) {
                System.out.println("created");
            } else {
                System.out.println(getArg(1) + " exists");
            }
        } catch (IOException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        }
    }

    @Override
    public boolean isMyCommand(final CommandString command) {
        if (name.equals(command.getArg(0))) {
            if (command.length() < numberOfArgs) {
                throw new InvalidCommandException("wrong type (" + name + " " + hint + ")");
            }
            args = command;
            return true;
        }
        return false;
    }
}
