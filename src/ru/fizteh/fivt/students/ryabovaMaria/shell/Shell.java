package ru.fizteh.fivt.students.ryabovaMaria.shell;

import java.util.Scanner;
import java.lang.reflect.Method;

public class Shell {
    static AbstractCommands command;
    
    public Shell(AbstractCommands myCommands) {
        command = myCommands;
    }
    
    public void processing(String currentString)throws Exception {
        String[] commands = currentString.split("[ \t\n\r]*;[ \t\n\r]*");
        for (int i = 0; i < commands.length; ++i) {
            commands[i] = commands[i].trim();
            command.lexems = commands[i].split("[ \t\n\r]+", 2);
            if (command.lexems.length == 0) {
                continue;
            }
            String curCommand = command.lexems[0];
            curCommand = curCommand.trim();
            if (!curCommand.isEmpty()) {
                Class c = command.getClass();
                try {
                    Method makeCommand = c.getMethod(curCommand);
                    makeCommand.invoke(command);
                } catch (NoSuchMethodException e) {
                    throw new Exception("Bad command"); 
                } catch (Exception e) {
                    throw new Exception(e.getCause().getMessage());
                }
            }
        }
    }
    
    public void interactive() {
        String currentString;
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            if (scan.hasNextLine()) {
                currentString = scan.nextLine();
                try {
                    processing(currentString);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            } else {
                Class c = command.getClass();
                try {
                    Method makeCommand = c.getMethod("exit");
                    makeCommand.invoke(command);
                } catch (NoSuchMethodException e) {
                    System.err.println("Bad command"); 
                } catch (Exception e) {
                    System.err.println(e.getCause().getMessage());
                }
            }
        }
    }
    
    public void packet(String[] args) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            temp.append(args[i]);
            temp.append(" ");
        }
        try {
            processing(temp.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
