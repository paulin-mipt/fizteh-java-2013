package ru.fizteh.fivt.students.adanilyak.shell;

import java.io.File;
import java.io.IOException;

public class CmdCd {
    private final RequestCommandType name = RequestCommandType.getType("cd");
    private final int amArgs = 1;

    public RequestCommandType getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    public void work(String arg, Shell shell) throws IOException {
        File newFile = new File(arg);
        if (newFile.isFile()) {
            throw new IOException("It's not directory");
        } else {
            shell.changeState(arg);
        }
    }
}
