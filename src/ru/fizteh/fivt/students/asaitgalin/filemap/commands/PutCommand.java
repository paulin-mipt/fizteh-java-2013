package ru.fizteh.fivt.students.asaitgalin.filemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class PutCommand extends DefaultCommand {
    private Table storage;

    public PutCommand(Table storage) {
        this.storage = storage;
    }

    @Override
    public String getName() {
        return "put";
    }

    @Override
    public void execute(String[] args) throws IOException {
        String prev = storage.put(args[1], args[2]);
        if (prev != null) {
            System.out.println("overwrite");
            System.out.println("old " + prev);
        } else {
            System.out.println("new");
        }
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
