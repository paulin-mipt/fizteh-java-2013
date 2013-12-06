package ru.fizteh.fivt.students.olgagorbacheva.shell;


import java.io.File; 
import java.io.IOException;
import java.nio.file.Paths;

public class ChangeDirectoryCommand implements Command{
      
      private String name = "cd";
      private final int argNumber = 1;
      
      public ChangeDirectoryCommand() {
            
      }
      
      public void execute(String args[], State state) throws ShellException {
            File f;
            if (Paths.get(args[1]).isAbsolute()) {
                  f = new File(args[1]);
            }else {
                  f = new File(new File(state.getState()), args[1]);
            }                             
            if (!f.exists()) {                  
                  throw new ShellException("cd: нет такого файла или директории");
            }
            if (!f.isDirectory()) {
                  throw new ShellException("cd: файл не является директорией");
            }
            if (!f.canExecute()) {
                  throw new ShellException("cd: директория не доступна");
            }
            try {
                  state.setState(f.getCanonicalPath());
            } 
            catch (IOException e) {
                  state.setState(f.getAbsolutePath());
            }           
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }
}