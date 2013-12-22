package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;
import java.io.IOException;

public class CommandDrop implements Command<MultiTableFatherState> {
    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public int getArgCount() {
        return 1;
    }

    @Override
    public void run(MultiTableFatherState state, String[] args) throws Exception {
        File db = state.getPath().resolve(args[0]).toFile();
        if (!db.exists()) {
            System.out.println(args[0] + " not exists");
        } else {
            try {
                state.deleteTable(args[0]);
            } catch (Exception e) {
                throw new IOException("Can't drop - it's not table.");
            }
            if (db.listFiles() != null) {
                for (File dbDir : db.listFiles()) {
                    if (dbDir.listFiles() != null) {
                        for (File entry : dbDir.listFiles()) {
                            entry.delete();
                        }
                    }
                    dbDir.delete();
                }
            }
            db.delete();
            System.out.println("dropped");
        }
    }
}
