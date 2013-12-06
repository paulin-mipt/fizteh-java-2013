package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.IOException;

public interface Command{
      
      public void execute(String args[], State state) throws IOException, IllegalArgumentException;            
      public String getName();           
      public int getArgNumber();                   

}