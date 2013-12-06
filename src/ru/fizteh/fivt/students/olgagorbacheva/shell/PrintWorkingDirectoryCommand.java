package ru.fizteh.fivt.students.olgagorbacheva.shell;

public class PrintWorkingDirectoryCommand implements Command  {
      private String name = "pwd";
      private final int argNumber = 0;
      
      public PrintWorkingDirectoryCommand() {
            
      }
      
      public void execute(String args[], State state) {
            System.out.println(state.getState());
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }

}
