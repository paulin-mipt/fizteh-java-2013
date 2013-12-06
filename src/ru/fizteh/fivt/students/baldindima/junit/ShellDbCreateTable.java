package ru.fizteh.fivt.students.baldindima.junit;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbCreateTable extends ShellIsItCommand {
    private Context context;

    public ShellDbCreateTable(Context nContext) {
        context = nContext;
    	setName("create");
        setNumberOfArgs(2);

    }

    public void run() throws IOException {
        if (context.provider.createTable(arguments[1]) != null) {
            System.out.println("created");

        } else {
            System.out.println(arguments[1] + " exists");
        }
    }

}

