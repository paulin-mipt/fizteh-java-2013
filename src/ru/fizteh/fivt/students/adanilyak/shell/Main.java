package ru.fizteh.fivt.students.adanilyak.shell;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class Main {
    public static Vector<String> intoCommandsAndArgs(String cmd, String delimetr) {
        String[] tokens = cmd.split(delimetr);
        Vector<String> result = new Vector<String>();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("") && !tokens[i].matches("\\s+")) {
                result.add(tokens[i]);
            }
        }
        return result;
    }

    private static boolean execute(String cmd, Shell shell) throws IOException {
        Vector<String> cmdAndArgs = intoCommandsAndArgs(cmd, " ");
        try {
            RequestCommandType cmdType = RequestCommandType.getType(cmdAndArgs.get(0));

            switch (cmdType) {
                case cd:
                    CmdCd curCmd = new CmdCd();
                    if (cmdAndArgs.size() != curCmd.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curCmd.work(cmdAndArgs.get(1), shell);
                    }
                    break;
                case mkdir:
                    CmdMkdir curMkDir = new CmdMkdir();
                    if (cmdAndArgs.size() != curMkDir.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curMkDir.work(cmdAndArgs.get(1), shell);
                    }
                    break;
                case pwd:
                    CmdPwd curPwd = new CmdPwd();
                    if (cmdAndArgs.size() != curPwd.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curPwd.work(shell);
                    }
                    break;
                case rm:
                    CmdRm curRm = new CmdRm();
                    if (cmdAndArgs.size() != curRm.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curRm.work(cmdAndArgs.get(1), shell);
                    }
                    break;
                case cp:
                    CmdCp curCp = new CmdCp();
                    if (cmdAndArgs.size() != curCp.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curCp.work(cmdAndArgs.get(1), cmdAndArgs.get(2), shell);
                    }
                    break;
                case mv:
                    CmdMv curMv = new CmdMv();
                    if (cmdAndArgs.size() != curMv.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curMv.work(cmdAndArgs.get(1), cmdAndArgs.get(2), shell);
                    }
                    break;
                case dir:
                    CmdDir curDir = new CmdDir();
                    if (cmdAndArgs.size() != curDir.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    } else {
                        curDir.work(shell);
                    }
                    break;
                case exit:
                    return false;
                default:
                    break;
            }
        } catch (Exception exc) {
            System.err.println(cmdAndArgs + ": " + exc.getMessage());
            if (shell.getTypeOfRunning()) {
                System.exit(2);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Shell shell = new Shell();

        if (args.length == 0) {
            shell.setInteractiveType();
            Scanner inputStream = new Scanner(System.in);
            boolean continueCycle = true;
            do {
                //Synchronize out and err streams
                // ---
                System.out.flush();
                System.err.flush();
                // ---

                System.out.print("$ ");
                String inputLine = inputStream.nextLine();
                inputLine.trim();
                Vector<String> commands = intoCommandsAndArgs(inputLine, ";");
                try {
                    for (String cmd : commands) {
                        if (!execute(cmd, shell)) {
                            continueCycle = false;
                        }
                    }
                    if (commands.isEmpty()) {
                        System.err.println("There are no suitable commands in line");
                    }
                } catch (Exception exc) {
                    System.err.println(exc.getMessage());
                    System.exit(1);
                }
            } while (continueCycle);
        } else {
            shell.setPackageType();
            StringBuilder packOfCommands = new StringBuilder();
            for (String cmdOrArg : args) {
                packOfCommands.append(cmdOrArg).append(" ");
            }
            String inputLine = packOfCommands.toString();
            Vector<String> commands = intoCommandsAndArgs(inputLine, ";");
            try {
                for (String cmd : commands) {
                    if (!execute(cmd, shell)) {
                        break;
                    }
                }
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
                System.exit(1);
            }
        }
    }
}
