package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class CopyCommand implements Command {
      
      private String name = "cp";
      private final int argNumber = 2;
      
      public CopyCommand() {
            
      }
      
      public void execute(String args[], State state) throws ShellException {
            File source;
            File destination;
            if (Paths.get(args[1]).isAbsolute()) {
                  source = new File(args[1]);
            }else {
                  source = new File(new File (state.getState()), args[1]);
            }   
            if (Paths.get(args[1]).isAbsolute()) {
                  destination = new File(args[2]);
            }else {
                  destination = new File(new File(state.getState()), args[2]);
            }
            if (!source.exists()) {                  
                  throw new ShellException("cp: нет такого файла или директории");
            }        
            if (destination.exists() && !destination.isDirectory()) {
                  throw new ShellException("cp: файл с данным именем уже существует");
            }
            if (destination.isDirectory() && destination.exists()) {
                  destination = new File(new File(destination.getAbsolutePath()), source.getName());
            }
            if (!destination.getParentFile().exists()) {
                  throw new ShellException("cp: нет такого файла или директории");
            }
            if (destination.exists()) {
                  throw new ShellException("cp: файл с данным именем уже существует");
            }
            try {
                  if (source.isDirectory()) {
                        if (!destination.mkdir()) {
                              throw new ShellException("cp: копирование не удалось");
                        }
                        if (!source.canRead() || !destination.canWrite()) {
                              throw new ShellException("cp: файл или директория не доступна");
                        }
                        String[] incFiles = source.list();
                        for (String file: incFiles) {
                              String[] str = new String[3];
                              str[0] = "cp";
                              str[1] = source.getAbsolutePath() + File.separator + file;
                              str[2] = destination.getCanonicalPath();
                              execute(str, state);
                        }
                  } else {
                        destination.createNewFile();
                        if (!source.canRead() || !destination.getParentFile().canWrite()) {
                              throw new ShellException("cp: файл или директория не доступна");
                        }
                        FileInputStream inputStream = new FileInputStream(source);
                        FileOutputStream outputStream = new FileOutputStream(destination);
                        byte[] buffer = new byte[1024];
                        int count = 0;
                        while ((count = inputStream.read(buffer)) > 0) {
                              outputStream.write(buffer, 0, count);
                        } 
                        inputStream.close();
                        outputStream.close();
                  }
                  
            }
            catch(IOException exp) {
                  throw new ShellException("cp: произошла ошибка при копировании");
            }
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }
}
