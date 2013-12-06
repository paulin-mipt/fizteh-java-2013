package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.File;
import java.nio.file.Paths;

public class RemoveCommand implements Command {
      private String name = "rm";
      private final int argNumber = 1;
      
      public RemoveCommand() {
            
      }
      
      public void execute(String args[], State state) throws ShellException {
            File f;
            if (Paths.get(args[1]).isAbsolute()) {
                  f = new File(args[1]);
            }else {
                  f = new File(new File(state.getState()), args[1]);
            }                             
            if (!f.exists()) {                  
                  throw new ShellException("rm: нет такого файла или директории");
            }
            if (f.isDirectory()) {
                  if (!f.canWrite()) {
                        throw new ShellException("rm: нет возможности удаления файлов");
                  }
                  if (!f.canRead()) {
                        throw new ShellException("rm: список файлов не доступен");
                  }
                  if (!f.canExecute()) {
                        throw new ShellException("rm: переход в директорию невозможен");
                  }
                  File[] incFiles = f.listFiles();
                  for (File i: incFiles) {
                        args[1] = i.getAbsolutePath();
                        execute(args, state);
                  }
            }            
            f.delete();
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }

}