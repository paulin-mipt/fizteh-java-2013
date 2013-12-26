package ru.fizteh.fivt.students.paulinMatavina.utils;

import java.util.Scanner;
import java.util.StringTokenizer;

public class CommandRunner {    
    public static void run(String[] args, State state) throws RuntimeException {  
        if (args.length > 0) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i]);
                str.append(" ");
            }
            int status = executeQueryLine(str.toString(), state);
            state.exitWithError(status);
        } else {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                if (scanner.hasNextLine()) {
                    String queryLine = scanner.nextLine();
                    executeQueryLine(queryLine, state);
                } else {
                    scanner.close();
                    state.exitWithError(0);
                }
            } 
        }
    }
    
    private static int execute(String query, State state) throws RuntimeException {
        query = query.trim();
        if (query.equals("")) {
            return 0;
        }  
        StringTokenizer token = new StringTokenizer(query);
        int tokenNum = token.countTokens();
        String nextCommand = token.nextToken();
        
        if (nextCommand.equals("exit") && tokenNum == 1) {
            state.exitWithError(0);
        }
        
        for (Command command : state.commands.values()) {
            if (command.getName().equals(nextCommand)) {
                if ((!command.spaceAllowed() && command.getArgNum() != tokenNum - 1) 
                        || (command.spaceAllowed() && command.getArgNum() >= tokenNum)) {
                    System.err.println("Wrong arguments number: " 
                            + command.getArgNum() + " expected");
                    return 1;
                }
                
                String[] args = new String[command.getArgNum()];
                if (command.spaceAllowed()) {
                    for (int i = 0; i < command.getArgNum() - 1; i++) {
                        args[i] = token.nextToken();
                    }
                    if (command.getArgNum() == 0 && command.spaceAllowed()) {
                        try {
                            return command.execute(args, state);
                        } catch (RuntimeException e) {
                            try {
                                int code = Integer.parseInt(e.getMessage());
                                state.exitWithError(code);
                            } catch (NumberFormatException e2) {
                                System.err.println(e.getMessage());
                                return 1;
                            } 
                        }
                    }
                    
                    int currSize = 0;
                    for (int i = 0; i < command.getArgNum(); i++) {
                        while (Character.toString(query.charAt(currSize)).matches("\\S")) {
                            currSize++;
                        }
                        while (Character.toString(query.charAt(currSize)).matches("\\s")) {
                            currSize++;
                        } 
                    }
                    args[command.getArgNum() - 1] = query.substring(currSize).trim();
                    try {
                        return command.execute(args, state);
                    } catch (RuntimeException e) {
                        try {
                            int code = Integer.parseInt(e.getMessage());
                            state.exitWithError(code);
                        } catch (NumberFormatException e2) {
                            System.err.println(e.getMessage());
                            return 1;
                        } 
                    }
                }                
                for (int i = 0; i < command.getArgNum(); i++) {
                    args[i] = token.nextToken();
                }
                try {
                    return command.execute(args, state);
                } catch (RuntimeException e) {
                    try {
                        int code = Integer.parseInt(e.getMessage());
                        state.exitWithError(code);
                    } catch (NumberFormatException e2) {
                        System.err.println(e.getMessage());
                        return 1;
                    } 
                }
            }
        }
        
        System.err.println("Wrong command " + nextCommand);
        return 1;
    }

    private static int executeQueryLine(String queryLine, State state) throws RuntimeException {
        Scanner scanner = new Scanner(queryLine);
        scanner.useDelimiter(";");
        while (scanner.hasNext()) {
            String query = scanner.next();
            int status = execute(query, state);
            if (status != 0) {
                scanner.close();
                return status;
            }  
        }
        scanner.close();
        return 0;
    }  
}
