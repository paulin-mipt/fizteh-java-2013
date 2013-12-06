package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.valentinbarishev.shell.CommandString;
import ru.fizteh.fivt.students.valentinbarishev.shell.InvalidCommandException;
import ru.fizteh.fivt.students.valentinbarishev.shell.SimpleShellCommand;
import java.text.ParseException;

public final class ShellDbPut  extends SimpleShellCommand {
    private Context context;

    public ShellDbPut(final Context newContext) {
        context = newContext;
        setName("put");
        setNumberOfArgs(3);
        setHint("usage: put <key> <value>");
    }

    @Override
    public void run() {
        if (context.table == null) {
            System.out.println("no table");
            return;
        }
        try {
            Storeable storeable = ((DataBase) context.table).putStoreable(getArg(1), getSpacedArg(2));
            if (storeable == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(context.provider.serialize(context.table, storeable));
            }
        } catch (ParseException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        }
    }

    @Override
    public boolean isMyCommand(final CommandString command) {
        if (name.equals(command.getArg(0))) {
            if (command.length() < numberOfArgs) {
                throw new InvalidCommandException(name + " " + hint);
            }
            args = command;
            return true;
        }
        return false;
    }
}
