package ru.fizteh.fivt.students.asaitgalin.filemap.commands;

import ru.fizteh.fivt.students.asaitgalin.filemap.SingleFileTable;
import ru.fizteh.fivt.students.asaitgalin.shell.DefaultCommand;

import java.io.IOException;

public class ExitCommand extends DefaultCommand {
    private SingleFileTable storage;

    public ExitCommand(SingleFileTable storage) {
        this.storage = storage;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void execute(String[] args) throws IOException {
        System.out.println("exit");
        storage.saveTable();
        System.exit(0);
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
