package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import java.util.ArrayList;

public class StartShell {
    public static void main(String[] args) {
        Shell shell = new Shell(new ShellState());
        ArrayList<Command> cmdList = new ArrayList<Command>();
        Command cmd = new CdCommand();
        cmdList.add(cmd);
        cmd = new PwdCommand();
        cmdList.add(cmd);
        cmd = new DirCommand();
        cmdList.add(cmd);
        cmd = new MakeDirCommand();
        cmdList.add(cmd);
        cmd = new RmCommand();
        cmdList.add(cmd);
        cmd = new MoveCommand();
        cmdList.add(cmd);
        cmd = new CopyCommand();
        cmdList.add(cmd);
        cmd = new ExitCommand();
        cmdList.add(cmd);
        shell.fillHashMap(cmdList);
        if (args.length == 0) {
            shell.interActive();
        } else {
            shell.pocket(args);
        }
    }
}
