package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.students.belousova.multifilehashmap.ChangesCountingTable;
import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileMap {

    public static Map<String, Command> commandList = new HashMap<String, Command>();

    public static void main(String[] args) {
        File data = new File(System.getProperty("fizteh.db.dir"), "db.dat");
        try {
            ChangesCountingTable table = new SingleFileTable(data);
            TableState tableState = new TableState(table);
            makeCommandList(tableState);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

    }

    private static void makeCommandList(TableState state) {
        addCommand(new CommandGet(state));
        addCommand(new CommandPut(state));
        addCommand(new CommandRemove(state));
        addCommand(new CommandExit());
    }

    private static void addCommand(Command command) {
        commandList.put(command.getName(), command);
    }
}
