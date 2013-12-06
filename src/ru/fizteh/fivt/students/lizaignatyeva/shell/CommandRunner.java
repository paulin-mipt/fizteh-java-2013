package ru.fizteh.fivt.students.lizaignatyeva.shell;

import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CommandRunner {
    static Hashtable<String, Command> commandsMap;
    File path;
    CommandFactory factory;
    public CommandRunner(File parentPath, Hashtable<String, Command> parentCommandsMap) {
        path = parentPath;
        commandsMap = parentCommandsMap;
    }

    public String getFullPath(String smallPath) {
        File myFile = new File(smallPath);
        if (myFile.isAbsolute()) {
            return smallPath;
        } else {
            return path.getAbsolutePath() + File.separator + smallPath;
        }
    }
    public String concatenateWithDelimiter(String[] strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            builder.append(strings[i]);
            if (i != strings.length - 1) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    public void run(String[] args) {
        factory = new CommandFactory(commandsMap);
        if (args.length != 0) {
            String commands = concatenateWithDelimiter(args, " ");
            try {
                runCommands(commands);
            } catch (Exception e) {
                System.exit(1);
            }
        } else {
            Scanner input = new Scanner(System.in);
            while (true) {
                try {
                    System.out.print(String.format("%s$ ", path.getCanonicalPath()));
                } catch (Exception e) {
                    System.err.println("Something went wrong!");
                    return;
                }
                if (!input.hasNextLine()) {
                    try {
                        factory.makeCommand("exit").run(new String[]{});
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                String commands = input.nextLine();
                if (commands.length() != 0) {
                   try {
                       runCommands(commands);
                   } catch (Exception e) {
                       System.err.println(e.getMessage());
                   }
                }
            }
        }
    }

    public void runCommands(String commands) throws Exception {
        String[] commandsList = commands.split(";");
        for (String commandWithArguments: commandsList) {
            String[] tokens = tokenizeCommand(commandWithArguments);
            if (tokens.length == 0) {
                continue;
            }
            Command command;
            try {
                command = factory.makeCommand(tokens[0]);
            } catch (Exception e) {
                throw e;
            }
            try {
                command.run(Arrays.copyOfRange(tokens, 1, tokens.length));
            } catch (Exception e) {
                throw new Exception(command.name + ": " + e.getMessage());
            }
        }
    }

    public String[] tokenizeCommand(String s) throws Exception {
        s = s.trim();
        StringTokenizer tokenizer = new StringTokenizer(s);
        String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); ++i) {
            result[i] = tokenizer.nextToken();
        }
        return result;
    }

}
