package ru.fizteh.fivt.students.mazanovArtem.shell;

import java.io.IOException;
import java.util.Map;

public interface Command {
    void exit();
    String helloString() throws IOException;
    Map<String, Object[]> linkCommand();
}
