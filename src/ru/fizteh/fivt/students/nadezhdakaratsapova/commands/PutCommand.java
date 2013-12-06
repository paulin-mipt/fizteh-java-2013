package ru.fizteh.fivt.students.nadezhdakaratsapova.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import java.io.IOException;
import java.text.ParseException;

public class PutCommand implements Command {
    private UniversalTableProvider curState;

    public PutCommand(UniversalTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "put";
    }

    public void execute(String[] args) throws IOException {
        if (curState.getCurTable() != null) {
            Object value = curState.getCurTable().get(args[1]);
            try {
                curState.getCurTable().put(args[1],
                        (Storeable) curState.getCurTable().valueConverter.convertStringToValueType(args[2]));

                if (value == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(curState.getCurTable().valueConverter.convertValueTypeToString(value));
                }
            } catch (ParseException e) {
                System.err.println("put command: " + e.getMessage());
            }
        } else {
            System.out.println("no table");
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 2);
    }
}
