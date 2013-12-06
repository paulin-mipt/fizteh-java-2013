package ru.fizteh.fivt.students.adanilyak.shell;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CmdDir {
    private final RequestCommandType name = RequestCommandType.getType("dir");
    private final int amArgs = 0;

    public RequestCommandType getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    public void work(Shell shell) throws IOException {
        DirectoryStream<Path> stream = Files.newDirectoryStream(shell.getState().toPath());
        for (Path file : stream) {
            System.out.println(file.getFileName().toString());
        }
    }
}
