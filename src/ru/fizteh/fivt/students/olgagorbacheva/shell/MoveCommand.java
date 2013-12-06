package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.File;
import java.nio.file.Paths;

public class MoveCommand implements Command {
      
      private String name = "mv";
      private final int argNumber = 2;
      
      public MoveCommand() {
            
      }
      
      public void execute(String args[], State state) throws ShellException {
            File source;
            File destination;
            if (Paths.get(args[1]).isAbsolute()) {
                  source = new File(args[1]);
            }else {
                  source = new File(new File(((State) state).getState()), args[1]);
            }   
            if (Paths.get(args[1]).isAbsolute()) {
                  destination = new File(args[2]);
            } else {
                  destination = new File(new File(((State) state).getState()), args[2]);
            }
            if (!destination.exists() && 
                        destination.getParentFile().getAbsolutePath().equals(source.getParentFile().getAbsolutePath())) {
                  source.renameTo(destination);
                  return;
            }
            try {
                  CopyCommand cp = new CopyCommand();
                  cp.execute(args, (State) state);
                  RemoveCommand rm = new RemoveCommand();
                  String[] argToRm = new String[2];
                  argToRm[1] = "rm";
                  argToRm[2] = args[2];
                  rm.execute(argToRm, (State) state);
            }
            catch(ShellException exp) {
                  throw new ShellException("mv:" + exp.getMessage().substring(3));
            }
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }
}
