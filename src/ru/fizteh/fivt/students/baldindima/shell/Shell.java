package ru.fizteh.fivt.students.baldindima.shell;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shell {

    private static ArrayList<ShellCommand> shellCommands;

    public Shell() {
        shellCommands = new ArrayList<ShellCommand>();
    }

    public final void addCommand(final ShellCommand command) {
        shellCommands.add(command);
    }

    public final void interactiveMode() throws IOException {
        //BufferedReader reader = null;
       // try {
            Scanner reader = new Scanner(System.in);
            String commands = "";
            System.out.print("$ ");
            if (reader.hasNextLine()){
            	commands = reader.nextLine();
            } else {
            	System.exit(0);
            }
            
            while (true) {
                try {
                    commands = commands.trim();
                    if (!commands.isEmpty()){
                    String[] command = commands.split("[\\s]*[;][\\s]*");
                    for (String element : command) {
                        executeCommand(element);
                    }
                    System.out.print("$ ");
                    if (reader.hasNextLine()){
                    	commands = reader.nextLine();
                    } else {
                    	System.exit(0);
                    }
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    System.out.print("$ ");
                    if (reader.hasNextLine()){
                    	commands = reader.nextLine();
                    } else {
                    	System.exit(0);
                    }
                    
                    
                } 
            }
            
                

    }

    public final void nonInteractiveMode(String[] args) throws IOException, ExitException {
        try {
            StringBuilder userCommands = new StringBuilder();

            for (String arg : args) {
                userCommands.append(arg + " ");
            }
            String commandsInOneString = userCommands.toString();
            commandsInOneString = commandsInOneString.trim();
            String[] toParseCommands = commandsInOneString.split("[\\s]*[;][\\s]*");
            for (String element : toParseCommands) {
                executeCommand(element);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
     
        }
    }

    static void executeCommand(String command) throws IOException, ExitException {
        String[] commands = command.split("[\\s]+");
        boolean isItCommand = false;
        for (int i = 0; i < shellCommands.size(); ++i) {
            if (shellCommands.get(i).isItCommand(commands)) {
                shellCommands.get(i).run();
                isItCommand = true;
            }
        }
        if (!isItCommand) {
            throw new IOException(command + " No such command");
        }
    }


}
