package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.IOException;
import java.util.*;

public class Shell{
      State state;
      Launcher launch;
           
      public Shell(State st) {
            state = st;
            launch = new Launcher();
      }
      
      public void execute(String[] args) {
            if (args.length == 0) {
                  interactiveMode();
            } else { 
                  batchMode(join(args, " "));
            }
      }
      
      public void execute() {
            interactiveMode();
      }

      private void interactiveMode() {
            boolean flag = true;  
            Scanner input = new Scanner(System.in);
            do {
                  System.out.print("$ "); 
                  String line = input.nextLine();
                  String[] commands = commandParse(line);
                  try {
                        for (String c: commands) {
                              String[] args = argumentParse(c);
                              flag = launch.execute(args, state);
                        }
                  }
                  catch (IOException | IllegalArgumentException exp) {
                        System.err.println(exp.getLocalizedMessage());
                  }
            } while(flag);
            input.close();
      }
      
      private void batchMode(String argument) {
            String[] commands = commandParse(argument);
            try {
                  for (String c: commands) {
                        String[] args = argumentParse(c);
                        launch.execute(args, state);
                  }
            }
            catch (IOException  exp) {
                  System.err.println(exp.getLocalizedMessage());
                  System.exit(-1);
            }
      }
      
      public static String join(String[] objects, String separator) {
            
            StringBuilder argument = new StringBuilder();
            boolean first = true;
            for (Object o: objects) {
                  if (!first) {
                        argument.append(separator);
                  } else first = false;
                  argument.append(o.toString());
            }
            return argument.toString();
      }
      
      private String[] commandParse(String arg) {
            String[] commands = (arg.trim()).split("\\s*;\\s*");
            return commands;
      }
      
      private String[] argumentParse(String arg) {
            String[] arguments = (arg.trim()).split("\\s+");
            return arguments;
      }
      
      public void addCommand(Command c) {
            launch.addCommand(c);
      }

}