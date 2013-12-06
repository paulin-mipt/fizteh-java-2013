package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.StateInterface;

public class Shell {
    
    private Shell() {
    }
    
    public static int startShell(String[] args, StateInterface st) {
        int code = 0;
        try {
            if (args.length > 0) {
                 Mode.batchMode(args, st);
             } else {
                 Mode.interactiveMode(st);
             }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            code = 1;
        } catch (ExitRuntimeException d) {
            try {
                st.commitDif(); 
            } catch (IOException e) {
                System.err.println("can't write data to file");
                code = 1;
            }
        }
        return code;
    }
}
