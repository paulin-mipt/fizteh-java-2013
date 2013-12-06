package ru.fizteh.fivt.students.mikhaylova_daria.shell;

import java.util.Scanner;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Parser {
    public static void parser(String[] arg, Class workingClass, HashMap<String, String> commands) throws Exception {
        boolean flag = true;
        Scanner input = new Scanner(System.in);
        String[] commandString;
        String inputString;
        while (flag) {
            if (arg.length == 0) {
                System.out.print(" $ ");
                if (input.hasNextLine()) {
                    inputString = input.nextLine();
                    commandString = inputString.split("[;]");
                    manager(commandString, false, workingClass, commands);
                } else {
                    System.exit(0);
                }
            } else {
                pack(arg, workingClass, commands);
                flag = false;
            }
        }
    }

    private static void manager(String[] commandString, boolean pack, Class workingClass,
                                HashMap<String, String> commands) throws Exception {
        int i;
        Object obj;
        obj = workingClass.newInstance();
        Class[] parametrTypes = new Class[] {String[].class};
        for (i = 0; i < commandString.length; ++i) {
            String[] command = commandString[i].trim().split("\\s+", 2);
            if (command[0].length() != 0) {
                Method currentMethod = null;
                if (!commands.containsKey(command[0])) {
                    System.err.println("Bad command");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    command[0] = commands.get(command[0]);
                }
                try {
                    currentMethod = workingClass.getMethod(command[0], parametrTypes);
                    try {
                        currentMethod.invoke(obj, (Object) command);
                    } catch (Exception e) {
                        System.err.println(e.getCause().getMessage());
                        if (pack) {
                            System.exit(1);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("parser: " + command[0] + ": " + "not found");
                    if (pack) {
                        System.exit(1);
                    }
                }
            }
        }
    }

    private static void pack(String[] arg, Class workingClass, HashMap<String, String> commands) throws Exception {
        StringBuilder builderArg;
        builderArg = new StringBuilder();
        int i;
        for (i = 0; i < arg.length; ++i) {
            builderArg.append(arg[i]);
            builderArg.append(" ");
        }
        String[] command = builderArg.toString().split("[;]");
        manager(command, true, workingClass, commands);

    }

}
