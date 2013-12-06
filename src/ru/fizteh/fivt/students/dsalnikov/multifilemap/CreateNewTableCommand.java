package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.students.dsalnikov.shell.Command;

import java.io.File;
import java.io.IOException;

public class CreateNewTableCommand implements Command {
    public String getName() {
        return "create";
    }

    public int getArgsCount() {
        return 1;
    }

    public void execute(Object sf, String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Incorrect usage of command create: 2 arguments expected");
        } else {
            MultiFileMapState fms = (MultiFileMapState) sf;
            File f = new File(fms.workingdirectory, args[1]);
            if (f.exists()) {
                System.out.println(args[1] + " exists");
                return;
            } else {
                f.mkdir();
                System.out.println("created");
            }
        }
    }
}
