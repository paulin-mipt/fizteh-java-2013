package ru.fizteh.fivt.students.asaitgalin.shell;

import ru.fizteh.fivt.students.asaitgalin.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class ShellUtils {
    private CommandTable table;

    public ShellUtils(CommandTable table) {
        this.table = table;
    }

    public void interactiveMode(InputStream in, PrintStream ps, PrintStream err) {
        Scanner scanner = new Scanner(in);
        ps.print("$ ");
        while (scanner.hasNext()) {
            try {
                table.executeCommandLine(scanner.nextLine());
            } catch (IOException ioe) {
                err.println(ioe.getMessage());
            }
            ps.print("$ ");
        }
    }

    public void batchMode(String[] args, PrintStream err) {
        String commandLine = StringUtils.join(Arrays.asList(args), " ");
        try {
            table.executeCommandLine(commandLine);
        } catch (IOException ioe) {
            err.println(ioe.getMessage());
            System.exit(1);
        }
    }

}

