package ru.fizteh.fivt.students.elenav.states;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.elenav.commands.AbstractCommand;
import ru.fizteh.fivt.students.elenav.commands.Command;

public abstract class FilesystemState {
    
    private final PrintStream stream;
    private String name;
    private File workingDirectory = null;
    public Provider provider = null;
    private final List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
    
    public void addCommand(AbstractCommand c) {
        commands.add(c);
    }
    
    protected FilesystemState(String n, File wd, PrintStream s) {
        stream = s;
        name = n;
        workingDirectory = wd;
    }
    
    public PrintStream getStream() {
        return stream;
    }
    
    public String getName() {
        return name;
    }
    
    public void execute(String commandArgLine) throws IOException {
        int correctCommand = 0;
        String[] args = commandArgLine.split("\\s+");
        int numberArgs = args.length - 1;
        for (Command c : commands) {
            if (c.getName().equals(args[0])) {
                if (c.getArgNumber() == numberArgs || c.getArgNumber() == -1) {
                    correctCommand = 1;
                    c.execute(args);
                    break;
                } else {
                    throw new IOException("Invalid number of args");
                }
            }
        }
        if (correctCommand == 0) {
            throw new IOException("Invalid command");
        }        
    }
    
    public void interactiveMode() {
        String command = "";
        final boolean flag = true;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.print("$ ");
            command = sc.nextLine();
            command = command.trim();
            String[] commands = command.split("\\s*;\\s*");
            for (String c : commands) {
                try {
                    execute(c);
                } catch (IOException | IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        } while (flag); 
    }
    
    public void run(String[] args) throws IOException {
        if (args.length == 0) {
            interactiveMode();
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb.append(s);
                sb.append(" ");
            }
            String monoString = sb.toString(); 
            
            monoString = monoString.trim();
            String[] commands = monoString.split("\\s*;\\s*");
            for (String command : commands) {
                execute(command);
            }
        }
    }

    public void setWorkingDirectory(File f) {
        workingDirectory = f;
    }
    
    public File getWorkingDirectory() {
        return workingDirectory;
    }
    
    protected List<AbstractCommand> getCommands() {
        return commands;
    }

    public abstract int commit();

    public abstract String put(String string, String string2) throws XMLStreamException, ParseException;
    
    public abstract Storeable put(String string, Storeable string2);

    public abstract String removeKey(String string);

    public abstract int rollback();

    public abstract int size();

    public abstract int getNumberOfChanges();

    public abstract void read() throws IOException;

    public void setName(String n) {
        name = n;
    }

    public abstract String getValue(String key) throws IOException;
    
}
