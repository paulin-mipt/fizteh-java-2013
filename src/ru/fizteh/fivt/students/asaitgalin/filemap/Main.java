package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.students.asaitgalin.filemap.commands.ExitCommand;
import ru.fizteh.fivt.students.asaitgalin.filemap.commands.GetCommand;
import ru.fizteh.fivt.students.asaitgalin.filemap.commands.PutCommand;
import ru.fizteh.fivt.students.asaitgalin.filemap.commands.RemoveCommand;
import ru.fizteh.fivt.students.asaitgalin.shell.CommandTable;
import ru.fizteh.fivt.students.asaitgalin.shell.ShellUtils;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        CommandTable table = new CommandTable();
        ShellUtils shellUtils = new ShellUtils(table);
        File dbName = new File(System.getProperty("fizteh.db.dir"), "db.dat");

        SingleFileTable entryTable = new SingleFileTable(dbName);
        try {
            entryTable.loadTable();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            System.exit(-1);
        }

        table.appendCommand(new PutCommand(entryTable));
        table.appendCommand(new GetCommand(entryTable));
        table.appendCommand(new RemoveCommand(entryTable));
        table.appendCommand(new ExitCommand(entryTable));

        if (args.length == 0) {
            shellUtils.interactiveMode(System.in, System.out, System.err);
        } else {
            shellUtils.batchMode(args, System.err);
        }


    }
}
