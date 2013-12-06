package ru.fizteh.fivt.students.elenav.shell;

import java.io.IOException;

public interface ShellFace {
        
    void changeDirectory(String name) throws IOException;

    void makeDirectory(String name) throws IOException;
    
    void rm(String name) throws IOException;
    
}
