package ru.fizteh.fivt.students.irinaGoltsman.shell;

import java.util.Scanner;

public class Shell {
    public static boolean isEmpty(String input) {
        String tmp = input.replaceAll(" ", "");
        tmp = tmp.replaceAll("\t", "");
        if (tmp.equals("")) {
            return true;
        }
        return false;
    }

    public static Code shell(String[] args) {
        if (args.length == 0) {  //Интерактивный режим
            Scanner scan = new Scanner(System.in);
            System.out.print("$ ");
            while (true) {
                String nameOfCommand = "exit";
                if (scan.hasNextLine()) {
                    nameOfCommand = scan.nextLine();
                } else {
                    break;
                }
                if (isEmpty(nameOfCommand)) {
                    System.out.print("$ ");
                    continue;
                }
                String[] commands = nameOfCommand.split(";");
                if (commands.length == 1) {
                    if (isEmpty(commands[0])) {
                        System.out.print("$ ");
                        continue;
                    }
                    Code codeOfCommand = MapOfCommands.commandProcessing(commands[0]);
                    if (codeOfCommand == Code.SYSTEM_ERROR) {
                        return Code.SYSTEM_ERROR;
                    } else if (codeOfCommand == Code.EXIT) {
                        return Code.EXIT;
                    }
                } else {
                    for (int i = 0; i < commands.length; i++) {
                        if (isEmpty(commands[i])) {
                            continue;
                        }
                        Code codeOfCommand = MapOfCommands.commandProcessing(commands[i]);
                        if (codeOfCommand == Code.SYSTEM_ERROR) {
                            return Code.SYSTEM_ERROR;
                        }
                    }
                }
                System.out.print("$ ");
            }
        } else {   //Пакетный режим
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i]);
                str.append(" ");
            }
            String input = str.toString();
            String[] commands = input.split(";");
            for (int i = 0; i < commands.length; i++) {
                if (isEmpty(commands[i])) {
                    continue;
                }
                Code codeOfCommand = MapOfCommands.commandProcessing(commands[i]);
                if (codeOfCommand != Code.OK) {
                    return codeOfCommand;
                }
            }
        }
        return Code.OK;
    }

    public static void main(String[] args) {
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Exit());
        cm.addCommand(new ShellCommands.Copy());
        cm.addCommand(new ShellCommands.Dir());
        cm.addCommand(new ShellCommands.MakeDir());
        cm.addCommand(new ShellCommands.ChangeDirectory());
        cm.addCommand(new ShellCommands.Move());
        cm.addCommand(new ShellCommands.PrintWorkDirectory());
        cm.addCommand(new ShellCommands.Remove());
        Code returnCode = shell(args);
        if (returnCode == Code.EXIT) {
            System.exit(0);
        }
        if (returnCode != Code.OK) {
            System.exit(1);
        }
    }
}
