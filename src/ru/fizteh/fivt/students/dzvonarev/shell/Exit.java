package ru.fizteh.fivt.students.dzvonarev.shell;

import java.util.ArrayList;

public class Exit implements CommandInterface {

    public void execute(ArrayList<String> args) {
        System.exit(0);
    }

}
