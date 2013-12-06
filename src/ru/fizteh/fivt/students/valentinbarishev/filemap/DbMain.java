package ru.fizteh.fivt.students.valentinbarishev.filemap;

import java.io.IOException;
import java.util.Scanner;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.shell.Shell;
import ru.fizteh.fivt.students.valentinbarishev.shell.InvalidCommandException;
import ru.fizteh.fivt.students.valentinbarishev.shell.CommandParser;
import ru.fizteh.fivt.students.valentinbarishev.shell.Main;

public class DbMain {
    private static Shell shell;

    private static void checkDbDir() {
        if (!System.getProperties().containsKey("fizteh.db.dir")) {
            System.err.println("Please set database directory!");
            System.err.println("-Dfizteh.db.dir=<directory name>");
            System.exit(1);
        }
    }

    private static void initShell() {
        try {
            shell = new Shell();

            TableProviderFactory factory = new MyTableProviderFactory();
            Context context = new Context(factory.create(System.getProperty("fizteh.db.dir")));

            shell.addCommand(new ShellDbPut(context));
            shell.addCommand(new ShellExit(context));
            shell.addCommand(new ShellDbGet(context));
            shell.addCommand(new ShellDbRemove(context));
            shell.addCommand(new ShellCreateTable(context));
            shell.addCommand(new ShellDropTable(context));
            shell.addCommand(new ShellUseTable(context));
            shell.addCommand(new ShellDbSize(context));
            shell.addCommand(new ShellDbCommit(context));
            shell.addCommand(new ShellDbRollback(context));

        } catch (IOException e) {
            System.out.println("init shell failed!");
            System.exit(1);
        }
    }

    private static void packetRun(final String[] args) {
        try {
            CommandParser parser = new CommandParser(args);
            while (!parser.isEmpty()) {
                shell.executeCommand(parser.getCommand());
            }
        } catch (InvalidCommandException|MultiDataBaseException|DataBaseWrongFileFormat|RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void interactiveRun() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(" $ ");
        while (true) {
            try {
                if (!scanner.hasNextLine()) {
                    throw new ShellExitException("Ctrl + D exit!");
                }

                String command = scanner.nextLine();

                if (Main.checkTerminate(command)) {
                    throw new ShellExitException("Ctrl + D exit or EOF!");
                }

                CommandParser parser = new CommandParser(command);
                if (!parser.isEmpty()) {
                    shell.executeCommand(parser.getCommand());
                }
            } catch (MultiDataBaseException|DataBaseWrongFileFormat|InvalidCommandException
                    |RuntimeException e) {
                System.err.println(e.getMessage());
            } finally {
                System.out.print(" $ ");
            }
        }
    }

    public static void main(final String[] args) {
        try {
            checkDbDir();
            initShell();

            if (args.length > 0) {
                packetRun(args);
            } else {
                interactiveRun();
            }

        } catch (DataBaseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ShellExitException e) {
            System.exit(0);
        } finally {
            System.exit(0);
        }
    }
}

