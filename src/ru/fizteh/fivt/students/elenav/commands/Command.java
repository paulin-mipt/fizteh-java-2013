package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;

public interface Command {
    
    String getName();
    
    int getArgNumber();
    
    void execute(String[] args) throws IOException;
}
