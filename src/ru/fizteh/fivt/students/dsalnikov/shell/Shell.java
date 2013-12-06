package ru.fizteh.fivt.students.dsalnikov.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;

public class Shell {
    private Object state;

    //годный шелл
    public Shell() {
        state = new ShellState();
    }

    public Shell(Object o) {
        state = o;
    }

    private Map<String, Command> CommandMap = new HashMap<String, Command>();

    public void setCommands(List<Command> cs) throws IllegalArgumentException {
        if (cs == null) {
            throw new IllegalArgumentException("this is madness");
        }
        for (Command Command : cs) {
            CommandMap.put(Command.getName(), Command);
        }
    }

    public void execute(String args[]) throws IOException {
        String concatenatedcmds = join(Arrays.asList(args), " ");
        String[] Commands = concatenatedcmds.split("\\s*;\\s*");
        for (String Command : Commands) {
            String[] cmdArgs = Command.split("\\s+");
            Command c = CommandMap.get(cmdArgs[0]);
            if (c == null) {
                throw new IllegalArgumentException("no such Command declared: " + cmdArgs[0]);
            }
            if (c.getArgsCount() + 1 != cmdArgs.length) {
                throw new IllegalArgumentException("wrong expression");
            }
            c.execute(state, cmdArgs);
        }
    }

    public static String join(Collection<?> objects, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object o : objects) {
            if (!first) {
                sb.append(separator);
            } else {
                first = false;
            }
            sb.append(o.toString());
        }
        return (sb.toString());
    }

    public void batchMode() {
        boolean flag = true;
        Scanner sc = new Scanner(System.in);
        while (flag) {
           System.out.print("$ ");

            String cmd[] = new String[1];

            cmd[0] = sc.nextLine();
            try {
                this.execute(cmd);
            } catch (FileAlreadyExistsException fae) {
                System.err.println(fae.getMessage());
            } catch (FileNotFoundException fnf) {
                System.err.println(fnf.getMessage());
                continue;
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
                continue;
            } catch (IllegalArgumentException iae) {
                System.err.println(iae.getMessage());
                continue;
            }
        }
    }

    public void commandMode(String[] args) {
        try {
            this.execute(args);
        } catch (FileAlreadyExistsException fae) {
            System.err.println(fae.getMessage());
            System.exit(1);
        } catch (FileNotFoundException fnf) {
            System.err.println(fnf.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.exit(1);
        }
    }
}


