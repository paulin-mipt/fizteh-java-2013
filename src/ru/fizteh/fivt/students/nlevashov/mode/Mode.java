package ru.fizteh.fivt.students.nlevashov.mode;

import java.util.*;
import java.io.IOException;

public class Mode {
    public static List<String> parse(String str, String separators) {
        String[] tokens = str.split(separators);
        List<String> tokensWithoutEmptyStrings = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                tokensWithoutEmptyStrings.add(tokens[i]);
            }
        }
        return tokensWithoutEmptyStrings;
    }

    public interface Executor {
        boolean execute(String cmd) throws IOException;
    }

    public static void start(String[] args, Executor exec) throws IOException {
        if (args.length == 0) {
            Scanner sc = new Scanner(System.in);
            boolean flag = true;
            do {
                try {
                    System.out.print("$ ");
                    String cmdline = sc.nextLine();
                    List<String> commands = parse(cmdline, ";");
                    for (String s : commands) {
                        if (!exec.execute(s)) {
                            flag = false;
                            break;
                        }
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("exit");
                    exec.execute("exit");
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } while (flag);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb.append(s).append(' ');
            }
            String cmdline = sb.toString();
            List<String> commands = parse(cmdline, ";");
            for (String s : commands) {
                if (!exec.execute(s)) {
                    break;
                }
            }
        }
    }
}
