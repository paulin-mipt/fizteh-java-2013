package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.File;

public class DirectoryCommand implements Command {
      private String name = "dir";
      private final int argNumber = 0;
      
      public DirectoryCommand() {
            
      }
      
      public void execute(String args[], State state) {
            File f = new File(state.getState());
            String[] flist = f.list();
            for (String incF: flist){
                  System.out.println(incF);
            }
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }

}
