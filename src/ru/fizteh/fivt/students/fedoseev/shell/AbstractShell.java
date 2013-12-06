package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AbstractShell extends AbstractFrame<ShellState> {
    public AbstractShell(File dir) {
        state = new ShellState();
        state.setCurState(dir);
    }

    @Override
    public Map<String, AbstractCommand> getCommands() {
        final ShellCdCommand cd = new ShellCdCommand();
        final ShellMkdirCommand mkdir = new ShellMkdirCommand();
        final ShellPwdCommand pwd = new ShellPwdCommand();
        final ShellRmCommand rm = new ShellRmCommand();
        final ShellCpCommand cp = new ShellCpCommand();
        final ShellMvCommand mv = new ShellMvCommand();
        final ShellDirCommand dir = new ShellDirCommand();
        final ShellExitCommand exit = new ShellExitCommand();

        return new HashMap<String, AbstractCommand>() {
            {
                put(cd.getCmdName(), cd);
                put(mkdir.getCmdName(), mkdir);
                put(pwd.getCmdName(), pwd);
                put(rm.getCmdName(), rm);
                put(cp.getCmdName(), cp);
                put(mv.getCmdName(), mv);
                put(dir.getCmdName(), dir);
                put(exit.getCmdName(), exit);
            }
        };
    }
}
