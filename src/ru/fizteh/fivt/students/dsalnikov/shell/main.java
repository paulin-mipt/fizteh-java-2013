package ru.fizteh.fivt.students.dsalnikov.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws IOException {

        Shell s = new Shell();

        ArrayList<Command> Commands = new ArrayList();
        RmCommand rm = new RmCommand();
        Commands.add(rm);
        CpCommand cp = new CpCommand();
        Commands.add(cp);
        DirCommand dir = new DirCommand();
        Commands.add(dir);
        ExitCommand exit = new ExitCommand();
        Commands.add(exit);
        MvCommand mv = new MvCommand();
        Commands.add(mv);
        CdCommand cd = new CdCommand();
        Commands.add(cd);
        PwdCommand pwd = new PwdCommand();
        Commands.add(pwd);
        MkdirCommand mkdir = new MkdirCommand();
        Commands.add(mkdir);
        s.setCommands(Commands);

        if (args.length == 0) {
            s.batchMode();
        } else {
            s.commandMode(args);
        }
    }
}

