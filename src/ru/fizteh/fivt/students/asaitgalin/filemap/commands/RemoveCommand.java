package ru.fizteh.fivt.students.asaitgalin.filemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class RemoveCommand extends DefaultCommand {
    private Table storage;

    public RemoveCommand(Table storage) {
        this.storage = storage;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void execute(String[] args) throws IOException {
        String value = storage.remove(args[1]);
        if (value != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
