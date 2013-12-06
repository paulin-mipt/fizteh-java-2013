package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ru.fizteh.fivt.students.msandrikova.multifilehashmap.State;


public class Shell {

    private Map<String, Command> commandsList;
    private File currentDirectory = new File("").getAbsoluteFile();
    private boolean isInteractive = false;
    private State myState;
    private String currentInstruction;
    
    private void initMap(Command[] commands) {
        Map<String, Command> m = new HashMap<String, Command>();
        for (Command c : commands) {
            m.put(c.getName(), c);
        }
        this.commandsList = Collections.unmodifiableMap(m);
    }
    
    public boolean getIsInteractive() {
        return this.isInteractive;
    }
    
    public State getState() {
        return this.myState;
    }
    
    public void setState(State myState) {
        this.myState = myState;
    }
    
    public File getCurrentDirectory() {
        return this.currentDirectory;
    }
    
    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
    
    public String getCurrentInstruction() {
        return this.currentInstruction;
    }
    
    private void executeOfInstructionLine(String instructionLine) {
        String[] instructionsList = new String[]{};
        String[] argumentsList;
        instructionsList = Utils.parseOfInstructionLine(instructionLine);
        for (String instruction : instructionsList) {
            this.currentInstruction = instruction;
            argumentsList = Utils.parseOfInstruction(instruction);
            if (argumentsList[0].equals("")) {
                continue;
            }
            if (this.commandsList.containsKey(argumentsList[0])) {
                this.commandsList.get(argumentsList[0]).execute(argumentsList, this);
            } else {
                Utils.generateAnError("Illegal command's name: \"" + argumentsList[0] 
                        + "\"", "", isInteractive);
                continue;
            }
        }    
    }
    
    public Shell(Command[] commands, String currentDirectory) {
        this.currentDirectory = new File(currentDirectory).getAbsoluteFile();
        if (!this.currentDirectory.exists()) {
            Utils.generateAnError("Given directory does not exist", "shell", false);
        }
        this.initMap(commands);
    }
    
    public void execute(String[] args) {
        String instructionLine = new String();
        if (args.length == 0) {
            this.isInteractive = true;
            Scanner scanner = new Scanner(System.in);
            
            while (!Thread.currentThread().isInterrupted()) {
                System.out.print("$ ");
                instructionLine = scanner.nextLine();
                this.executeOfInstructionLine(instructionLine);
            }
            scanner.close();
        } else {
            instructionLine = Utils.joinArgs(Arrays.asList(args), " ");
            this.executeOfInstructionLine(instructionLine);
        }
    }
}
