package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;
import java.util.ArrayList;

public class CommandCreate extends DataBaseCommand {
    public CommandCreate(TableState state) {
        super(state);
        name = "create";
        numberOfArguments = 2;
    }

    @Override
    public void executeProcess(String[] input) {
        String name = input[1];
        String values = input[2];
        ArrayList<Class<?>> classNew = new ArrayList<Class<?>>();
        String[] result = ParseValue.parse(values);
        for (int i = 0; i < result.length; ++i) {
            if (!result[i].isEmpty()) {
                classNew.add(state.getTableProvider().getNameClass(result[i]));
            }
        }
        try {
            if (state.getTableProvider().createTable(name, classNew) == null) {
                System.out.println(name + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException | IOException e) {
            state.printError(e.getMessage());
            return;
        }

    }

}
