package ru.fizteh.fivt.students.krivchansky.shell;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Shell {
    private final Map<String, Commands> availableCommands;
    private static final String greeting = "$ ";
    
    public Shell (Commands[] commands) {
        Map <String, Commands> tempCommands = new HashMap<String, Commands>();
        for (Commands temp : commands) {
            tempCommands.put(temp.getCommandName(), temp);
        }
        availableCommands = Collections.unmodifiableMap(tempCommands);
    }
    
    private void runCommand(String[] data, Shell.ShellState state) throws SomethingIsWrongException {
        if (data[0].length() == 0) {
            throw new SomethingIsWrongException ("Empty string.");
        }
        Commands usedOne = availableCommands.get(data[0]);
        if (usedOne == null) {
            throw new SomethingIsWrongException ("Unknown command.");
        } else if (data.length - 1 != usedOne.getArgumentQuantity() ) {
            throw new SomethingIsWrongException ("Wrong number of arguments. Correct argument quantity = " + (data.length-1) + 
                    "\nTo correctly run this command use " + usedOne.getArgumentQuantity() + " arguments.");
        }
        String[] commandArguments = Arrays.copyOfRange(data, 1, data.length);
        usedOne.implement(commandArguments, state);
    }
    
    private String[] splitLine(String str) {
        str = str.trim();
        String[] toReturn = str.split("\\s*;\\s*", -2);
        return toReturn;
    }
    private void runLine(String line, Shell.ShellState state) throws SomethingIsWrongException {
        String[] splitted = splitLine(line);
        int count = splitted.length - 1;
        for (String temp : splitted) {
            --count;
            if (count >= 0) {
                runCommand(temp.split("\\s+"), state);     //This thing is needed to check if after last ";"
            } else if (count < 0) {                        //situated a command or an empty string. So it
                if (temp.length() != 0) {                  //won't throw a "Wrong command" exception when it
                    runCommand(temp.split("\\s+"), state); //looks like: "dir; cd ..; dir;" neither it will
                }                                          //loose a "dir" in: "dir; cd ..; dir; dir".
            }                                              //So it does nothing if it is the end, but performs
        }                                                  //if there is a command after last ";".
    }
    
    public class ShellState {
        private  String currentDirectory;
        public ShellState(String currentDirectory) {
            this.currentDirectory = currentDirectory;
        }
        public String getCurDir() {
            return currentDirectory;
        }      
        void changeCurDir(String newCurDir) {
            currentDirectory = newCurDir;
        }
    }
    
    private void consoleWay(Shell.ShellState state) {
        Scanner forInput = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {
            System.out.print(state.getCurDir() + greeting);
            try {
                runLine(forInput.nextLine(), state);                  
            } catch (SomethingIsWrongException exc) {
                if (exc.getMessage().equals("EXIT")) {
                    forInput.close();
                    System.exit(0);
                } else {
                    System.err.println(exc.getMessage());
                }
            }
        }
        forInput.close();
    }
    
    public static void main(String[] args) {
        Commands[] commands = { new WhereAreWeCommand(), new RemoveCommand(), new ChangeDirectoryCommand(), new MakeDirectoryCommand(), new MoveCommand(), new CopyCommand(), new DirectoryInfoCommand(), new ExitCommand() };
        Shell shell = new Shell(commands);
        Shell.ShellState state = shell.new ShellState(System.getProperty("user.dir"));
        if (args.length != 0) {
            String arg = UtilMethods.uniteItems(Arrays.asList(args), " ");
            try {
                shell.runLine(arg, state);                  
            } catch (SomethingIsWrongException exc) {
                if (exc.getMessage().equals("EXIT")) {
                    System.exit(0);
                } else {
                    System.err.println(exc.getMessage());
                    System.exit(-1);
                }
            }
        } else {
            shell.consoleWay(state);
        }
        System.exit(0);
    }
    
    

}
