package ru.fizteh.fivt.students.olgagorbacheva.shell;

public class ExitCommand implements Command  {
      private String name = "exit";
      private final int argNumber = 0;
      
      
      public void execute(String args[], State state) {
            System.out.println("exit");
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }

}
