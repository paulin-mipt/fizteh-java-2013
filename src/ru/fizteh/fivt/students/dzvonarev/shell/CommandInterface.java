package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.IOException;
import java.util.ArrayList;

public interface CommandInterface {

    void execute(ArrayList<String> args)
            throws IOException, IllegalArgumentException, IllegalStateException;

}
