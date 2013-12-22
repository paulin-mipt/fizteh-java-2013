package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CmdCreate implements Command<StoreableState> {

    @Override
    public String getName() {

        return "create";
    }

    @Override
    public void executeCmd(StoreableState inState, String[] args) throws IOException {

        if (args.length != 2) {
            throw new IOException("incorrect number of arguments");
        }

        List<Class<?>> columnTypes = new ArrayList<Class<?>>();

        String[] types = args[1].trim().split("\\s+");
        try {
            for (String type : types) {

                if (StoreableEnum.dataBaseCorrectNamesOfClasses.contains(type)) {
                    throw new IllegalArgumentException("wrong type");
                }
                columnTypes.add(StoreableUtils.convertStringToClass(type));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("wrong type (" + args[1] + ")");
        }
        try {
            if (inState.createTable(args[0], columnTypes) != null) {
                System.out.println("created");
            } else {
                System.out.println(args[0] + " exists");
            }
        } catch (Exception e) {
            throw new IOException("error with create of table");
        }
    }
}
