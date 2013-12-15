package ru.fizteh.fivt.students.baldindima.junit;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbCreateTable extends ShellIsItCommand {
    private Context context;

    public ShellDbCreateTable(Context nContext) {
        context = nContext;
    	setName("create");
        setNumberOfArgs(3);

    }

    public void run() throws IOException {
    	
    	String args = join(arguments);
    	try{
        if (context.provider.createTable(arguments[1], BaseSignature.getTypes(args)) != null) {
            System.out.println("created");

        } else {
            System.out.println(arguments[1] + " exists");
        }
    	} catch (IOException e) {
    		System.out.println("wrong type (" + e.getMessage() +")");
    	} catch (IllegalArgumentException e) {
    		System.out.println ("wrong type (" + e.getMessage() +")");
    	}
    	
    }
    public boolean isItCommand(final String[] commands) throws IOException{
		if (commands[0].equals(name)){
		if (commands.length < numberOfArgs){
			throw new IOException("Invalid number of arguments");
		
		}
		arguments = commands;
		return true;
		}
		return false;
	}

}

