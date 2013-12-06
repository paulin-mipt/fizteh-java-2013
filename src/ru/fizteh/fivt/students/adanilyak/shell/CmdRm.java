package ru.fizteh.fivt.students.adanilyak.shell;

import java.io.File;
import java.io.IOException;

public class CmdRm {
    private final RequestCommandType name = RequestCommandType.getType("rm");
    private final int amArgs = 1;

    public RequestCommandType getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    private void recursiveDeletePart(File startPoint) throws Exception {
        File[] listOfFiles = startPoint.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                recursiveDeletePart(file);
            }
        }

        if (!startPoint.delete()) {
            throw new Exception("Can not delete directory, unknown error");
        }
    }

    public void work(String arg, Shell shell) throws Exception {
        File newFile = new File(shell.getState(), arg);
        newFile = newFile.toPath().normalize().toFile();
        if (!newFile.exists()) {
            throw new IOException("File or directory do not exist");
        }
        recursiveDeletePart(newFile);
    }
}
