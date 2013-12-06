package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;
import java.util.Map;

public interface CommandAbstract {
    String startShellString() throws IOException;
    Map<String, Object[]> mapComamnd();
}
