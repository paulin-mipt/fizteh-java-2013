package ru.fizteh.fivt.students.surakshina.filemap;

import java.text.ParseException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class CommandPut extends DataBaseCommand {
    public CommandPut(TableState state) {
        super(state);
        name = "put";
        numberOfArguments = 2;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            String key = input[1];
            Storeable value = null;
            Storeable result = null;
            try {
                value = state.getTableProvider().deserialize(state.getTable(), input[2]);
            } catch (ParseException e) {
                state.printError(e.getMessage());
                return;
            }
            try {
                result = state.getTable().put(key, value);
            } catch (ColumnFormatException e) {
                state.printError(e.getMessage());
                return;
            } catch (IllegalArgumentException e2) {
                state.printError(e2.getMessage());
                return;
            }
            if (result != null) {
                System.out.println("overwrite");
                System.out.println(JSONSerializer.serialize(state.getTable(), result));
            } else {
                System.out.println("new");
            }
        } else {
            System.out.println("no table");
        }
    }

}
