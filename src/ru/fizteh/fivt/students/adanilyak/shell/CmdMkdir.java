package ru.fizteh.fivt.students.adanilyak.shell;

import java.io.File;

public class CmdMkdir {
    private final RequestCommandType name = RequestCommandType.getType("mkdir");
    private final int amArgs = 1;

    public RequestCommandType getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    public void work(String arg, Shell shell) throws Exception {
        File newDir = new File(shell.getState(), arg);
        newDir = newDir.toPath().normalize().toFile();
        if (!newDir.getCanonicalFile().mkdir()) {
            throw new Exception("Can not create directory: " + arg);
        }
    }
}
