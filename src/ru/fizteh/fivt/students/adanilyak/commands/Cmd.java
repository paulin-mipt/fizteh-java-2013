package ru.fizteh.fivt.students.adanilyak.commands;

import java.io.IOException;
import java.util.List;

/**
 * User: Alexander
 * Date: 20.10.13
 * Time: 22:39
 */
public interface Cmd {
    String getName();

    int getAmArgs();

    void work(List<String> args) throws IOException;
}
