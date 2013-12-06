package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 19:40
 * To change this template use File | Settings | File Templates.
 */
public interface Commands {
    String getName();
    void perform(String[] s) throws IOException;
}
