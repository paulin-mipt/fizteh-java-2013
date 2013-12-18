package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.students.musin.shell.Shell;

import java.io.PrintStream;
import java.util.ArrayList;


public class ShellServerDatabaseHandler {
    private DatabaseServer server;

    public ShellServerDatabaseHandler(FileMapProvider provider) {
        server = new DatabaseServer(provider);
    }

    ArrayList<String> parseArguments(int argCount, String argString) {
        ArrayList<String> args = new ArrayList<String>();
        int argsRead = 0;
        String last = "";
        int start = 0;
        for (int i = 0; i < argString.length(); i++) {
            if (Character.isWhitespace(argString.charAt(i))) {
                if (start != i) {
                    args.add(argString.substring(start, i));
                    argsRead++;
                }
                start = i + 1;
                if (argsRead == argCount - 1) {
                    last = argString.substring(start, argString.length());
                    break;
                }
            }
        }
        last = last.trim();
        if (!last.equals("")) {
            args.add(last);
        }
        return args;
    }

    void printException(Throwable e, PrintStream errorLog) {
        if (e.getCause() != null) {
            printException(e.getCause(), errorLog);
        }
        for (Throwable suppressed : e.getSuppressed()) {
            printException(suppressed, errorLog);
        }
        if (e.getMessage() != null && !e.getMessage().equals("")) {
            errorLog.println(e.getMessage());
        }
    }

    private Shell.ShellCommand[] commands = new Shell.ShellCommand[]{
            new Shell.ShellCommand("start", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        shell.writer.println("start: Too many arguments");
                        return -1;
                    }
                    if (server.isStarted()) {
                        shell.writer.println("already started");
                    }
                    int newPort = 10001;
                    if (args.size() == 1) {
                        try {
                            newPort = Integer.parseInt(args.get(0));
                        } catch (NumberFormatException e) {
                            printException(e, shell.writer);
                            return -1;
                        }
                    }
                    try {
                        server.start(newPort);
                    } catch (InterruptedException e) {
                        shell.writer.println("Critical error: main thread interrupted");
                        return -1;
                    } catch (DatabaseServer.ServerStartException e) {
                        shell.writer.println(String.format("not started: %s", e.getMessage()));
                        return -1;
                    }
                    shell.writer.println(String.format("started at %d", newPort));
                    return 0;
                }
            }),
            new Shell.ShellCommand("stop", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("stop: Too many arguments");
                        return -1;
                    }
                    if (!server.isStarted()) {
                        shell.writer.println("not started");
                        return -1;
                    }
                    int port;
                    try {
                        port = server.stop();
                    } catch (InterruptedException e) {
                        shell.writer.println("Critical error: main thread interrupted");
                        return -1;
                    }
                    shell.writer.println(String.format("stopped at %d", port));
                    return 0;
                }
            }),
            new Shell.ShellCommand("listusers", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 0) {
                        shell.writer.println("listusers: Too many arguments");
                        return -1;
                    }
                    if (!server.isStarted()) {
                        shell.writer.println("not started");
                        return -1;
                    }
                    for (String entry : server.listConnections()) {
                        shell.writer.println(entry);
                    }
                    return 0;
                }
            })
    };

    public void integrate(Shell shell) {
        for (int i = 0; i < commands.length; i++) {
            shell.addCommand(commands[i]);
        }
        shell.addExitFunction(new Shell.ShellCommand(null, new Shell.ShellExecutable() {
            @Override
            public int execute(Shell shell, ArrayList<String> args) {
                if (server.isStarted()) {
                    try {
                        server.stop();
                    } catch (InterruptedException e) {
                        //Exit function nothing to do
                    }
                }
                return 0;
            }
        }));
    }
}
