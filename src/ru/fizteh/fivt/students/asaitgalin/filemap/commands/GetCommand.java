package ru.fizteh.fivt.students.asaitgalin.filemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class GetCommand extends DefaultCommand {
    private Table storage;

    public GetCommand(Table storage) {
        this.storage = storage;
    }

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public void execute(String[] args) throws IOException {
        String value = storage.get(args[1]);
        if (value != null) {
            System.out.println("found");
            System.out.println(value);
        } else {
            System.out.println("not found");
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
