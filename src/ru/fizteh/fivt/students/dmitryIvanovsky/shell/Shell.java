package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;

public class Shell {

    public static void main(String[] args) throws IOException {
        //args = new String[]{"cd /home/deamoon/Music;", "cp dir3 dir4"};
        CommandShell fileCommand = new CommandShell();
        CommandLauncher sys = null;
        try {
            sys = new CommandLauncher(fileCommand);
        } catch (Exception e) {
            System.err.println("Не реализован метод из CommandShell");
            System.exit(1);
        }
        try {
            Code res = sys.runShell(args);
            if (res == Code.ERROR) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Ошибка выполнения");
            System.exit(1);
        }

    }

}
