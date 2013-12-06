package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.File;

public class MakeDirectoryCommand implements Command {
      private String name = "mkdir";
      private final int argNumber = 1;
      
      public MakeDirectoryCommand() {
            
      }
      
      public void execute(String args[], State state) throws ShellException {
            File f = new File(new File(state.getState()), args[1]);                            
            if (f.exists()) {                  
                  throw new ShellException("mkdir: файл с таким именем уже существует");
            }
            if (!f.mkdir()) {
                  throw new ShellException("mkdir: создание директории невозможно");
            }
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }

}