package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public interface StateInterface {

    void add(Command com);

    InputStream getInputStream();

    PrintStream getOutputStream();

    File getCurrentDir();

    void setCurrentDir(File currentDir) throws IOException;

    void checkAndExecute(String[] args) throws IOException;

    int commitDif() throws IOException;

}
