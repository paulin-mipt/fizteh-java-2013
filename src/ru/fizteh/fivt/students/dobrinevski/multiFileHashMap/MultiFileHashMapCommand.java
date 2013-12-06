package ru.fizteh.fivt.students.dobrinevski.multiFileHashMap;

import ru.fizteh.fivt.students.dobrinevski.shell.Command;

import java.io.File;

public abstract class MultiFileHashMapCommand extends Command {
    public MyMultiHashMap parent;
    public File root;

    public MultiFileHashMapCommand(int argCount, MyMultiHashMap prnt, File realRoot) {
        super(argCount);
        parent = prnt;
        root = realRoot;
    }
}
