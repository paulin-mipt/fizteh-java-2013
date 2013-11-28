package ru.fizteh.fivt.students.mazanovArtem.multifilehashmap;

import ru.fizteh.fivt.students.mazanovArtem.shell.ShellMain;

import java.io.File;

public class FileMap {
    public static void main(String[] args) {
        //File tmp = new File("/home/tema/Documents/java");
        String prop = System.getProperty("fizteh.db.dir");
        if (prop == null) {
            System.out.println("directory doesn't exist");
            System.exit(1);
        }
        File tmp = new File(System.getProperty("fizteh.db.dir"));
        if (!tmp.exists()) {
            System.out.println("Directory doesn't exist");
            System.exit(1);
        }
        FileMapSystem file = new FileMapSystem(tmp);
        ShellMain sys = null;
        try {
            sys = new ShellMain(file);
        } catch (Exception e) {
            System.out.println("Не реализован метод из FileMapSystem");
        }
        try {
            int result = sys.runShell(args);
            if (result == 1) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Ошибка выполнения команды");
            System.exit(1);
        }
    }
}


