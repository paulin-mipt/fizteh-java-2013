package ru.fizteh.fivt.students.mazanovArtem.multifilehashmap;

import ru.fizteh.fivt.students.mazanovArtem.shell.ShellMain;

import java.io.File;

public class FileMap {
    public static void main(String[] args) {
        //File tmp = new File("/home/tema/Documents/java/db.dat");
        File tmp = new File(System.getProperty("fizteh.db.dir"));
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


