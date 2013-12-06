package ru.fizteh.fivt.students.irinapodorozhnaya.shell;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;


public class StateShell extends State {
    
    public StateShell(InputStream in, PrintStream out) {
        super(in, out);
        try {
            setCurrentDir(new File("."));
        } catch (IOException e) {
            //can't be thrown
        }
        add(new CommandDirectory(this));
        add(new CommandChangeDirectory(this));
        add(new CommandRemove(this));
        add(new CommandMove(this));
        add(new CommandPrintWorkingDirectory(this));
        add(new CommandCopy(this));
        add(new CommandExit(this));
        add(new CommandMakeDirectory(this));
    }

    protected File getFileByName(String path) {
        File f = new File(path);
        if (f.isAbsolute()) {
            return f;
        } else {
            return new File(this.getCurrentDir(), path);
        }
    }
}
