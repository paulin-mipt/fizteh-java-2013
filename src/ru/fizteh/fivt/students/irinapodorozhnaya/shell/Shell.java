package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.ExitRuntimeException;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Mode;

public class Shell {
     public static void main(String[] args) {
        int code = 0;
         try {
            StateShell st = new StateShell(System.in, System.out);
                if (args.length > 0) {
                    Mode.batchMode(args, st);
                } else {
                    Mode.interactiveMode(st);
                }
        } catch (ExitRuntimeException e) {
            //exit
        } catch (IOException e) {
            code = 1;
        }
        System.exit(code);
     }
}
