package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.students.belousova.filemap.CommandExit;
import ru.fizteh.fivt.students.belousova.filemap.CommandGet;
import ru.fizteh.fivt.students.belousova.filemap.CommandPut;
import ru.fizteh.fivt.students.belousova.filemap.CommandRemove;
import ru.fizteh.fivt.students.belousova.multifilehashmap.*;
import ru.fizteh.fivt.students.belousova.shell.Command;
import ru.fizteh.fivt.students.belousova.utils.ShellUtils;

import java.util.HashMap;
import java.util.Map;

public class StorableShell {
    private Map<String, Command> commandList = new HashMap<>();

    public void run(String[] args, StorableState state) {
        makeCommandList(state);
        if (args.length == 0) {
            ShellUtils.interactiveMode(System.in, commandList);
        } else {
            ShellUtils.batchMode(args, commandList);
        }
    }

    private void makeCommandList(StorableState state) {
        addCommand(new CommandCreateStorable(state));
        addCommand(new CommandDrop(state));
        addCommand(new CommandUse(state));
        addCommand(new CommandPut(state));
        addCommand(new CommandGet(state));
        addCommand(new CommandRemove(state));
        addCommand(new CommandCommit(state));
        addCommand(new CommandRollback(state));
        addCommand(new CommandSize(state));
        addCommand(new CommandExit());
    }

    private void addCommand(Command command) {
        commandList.put(command.getName(), command);
    }
}
