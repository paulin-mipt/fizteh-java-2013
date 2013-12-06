package ru.fizteh.fivt.students.paulinMatavina;

import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        ShellEnvironment env = new ShellEnvironment();
        if (args.length > 0) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i]);
                str.append(" ");
            }
            int status = env.executeQueryLine(str.toString());
            System.exit(status);
        } else {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                if (scanner.hasNextLine()) {
                    String queryLine = scanner.nextLine();
                    env.executeQueryLine(queryLine);
                } else {
                    scanner.close();
                    return;
                }
            }
        }
    }
}
