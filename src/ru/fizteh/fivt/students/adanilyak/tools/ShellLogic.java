package ru.fizteh.fivt.students.adanilyak.tools;

import ru.fizteh.fivt.students.adanilyak.commands.Cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * User: Alexander
 * Date: 20.10.13
 * Time: 23:15
 */
public class ShellLogic {

    /**
     * @param parserAndExecutor - задает парсер
     *                          1 = MultiFileHashMapParserAndExecutor
     *                          2 = StoreableParserAndExecutor
     *                          Использование парсера не предназначенного для конкретной задачи ведет к ошибке!
     */
    private static List<String> parseCmdAndArgs(String inputLine, final Integer parserAndExecutor) throws IOException {
        if (parserAndExecutor == 1) {
            return MultiFileCmdParseAndExecute.intoCommandsAndArgs(inputLine, ";");
        } else if (parserAndExecutor == 2) {
            return StoreableCmdParseAndExecute.splitByDelimiter(inputLine, ";");
        } else {
            throw new IOException("package mode: parser and executor fail, can not start suitable method");
        }
    }

    /**
     * @param parserAndExecutor - задает парсер
     *                          1 = MultiFileHashMapParserAndExecutor
     *                          2 = StoreableParserAndExecutor
     *                          Использование парсера не предназначенного для конкретной задачи ведет к ошибке!
     */
    private static void execute(String command, Map<String, Cmd> cmdList, final Integer parserAndExecutor)
            throws IOException {
        if (parserAndExecutor == 1) {
            MultiFileCmdParseAndExecute.execute(command, cmdList);
        } else if (parserAndExecutor == 2) {
            StoreableCmdParseAndExecute.execute(command, cmdList);
        } else {
            throw new IOException("package mode: parser and executor fail, can not start suitable method");
        }
    }

    /**
     * @param parserAndExecutor - задает парсер
     *                          1 = MultiFileHashMapParserAndExecutor
     *                          2 = StoreableParserAndExecutor
     *                          Использование парсера не предназначенного для конкретной задачи ведет к ошибке!
     */
    public static void packageMode(String[] args, Map<String, Cmd> cmdList, PrintStream out, PrintStream err,
                                   final Integer parserAndExecutor) {
        StringBuilder packOfCommands = new StringBuilder();
        for (String cmdOrArg : args) {
            packOfCommands.append(cmdOrArg).append(" ");
        }
        String inputLine = packOfCommands.toString();
        try {
            List<String> commandWithArgs = parseCmdAndArgs(inputLine, parserAndExecutor);
            for (String command : commandWithArgs) {
                execute(command, cmdList, parserAndExecutor);
            }
        } catch (Exception exc) {
            err.println(exc.getMessage());
            System.exit(3);
        }
    }

    /**
     * @param parserAndExecutor - задает парсер
     *                          1 = MultiFileHashMapParserAndExecutor
     *                          2 = StoreableParserAndExecutor
     *                          Использование парсера не предназначенного для конкретной задачи ведет к ошибке!
     */
    public static void interactiveMode(InputStream in, Map<String, Cmd> cmdList, PrintStream out, PrintStream err,
                                       final Integer parserAndExecutor) {
        Scanner inputStream = new Scanner(in);
        do {
            //Synchronize out and err streams
            // ---
            out.flush();
            err.flush();
            // ---

            out.print("$ ");
            String inputLine = inputStream.nextLine();
            try {
                List<String> commandWithArgs = parseCmdAndArgs(inputLine, parserAndExecutor);
                for (String command : commandWithArgs) {
                    //Synchronize out and err streams
                    // ---
                    out.flush();
                    err.flush();
                    // ---
                    execute(command, cmdList, parserAndExecutor);
                }
            } catch (Exception exc) {
                err.println(exc.getMessage());
            }
        } while (!Thread.currentThread().isInterrupted());
    }
}
