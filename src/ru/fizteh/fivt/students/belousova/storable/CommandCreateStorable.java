package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandCreateStorable implements Command {
    StorableState state = null;

    public CommandCreateStorable(StorableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void execute(String[] args) throws IOException {
        String key = args[1];
        if (state.getTable(key)) {
            System.out.println(key + " exists");
        } else {
            String signature = args[2].trim();
            if (!signature.startsWith("(") || !signature.endsWith(")")) {
                throw new IOException("wrong argument type");
            }
            signature = signature.substring(1, signature.length() - 1);
            String[] types = signature.split("\\s+");
            state.createTableWithSignature(key, types);
            System.out.println("created");
        }
    }

    @Override
    public int getArgCount() {
        return 2;
    }
}
