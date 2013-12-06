package ru.fizteh.fivt.students.vorotilov.shell;

import java.util.LinkedList;

public class PackageInput extends ConsoleInput {

    public PackageInput(String[] packageInput) {
        commandsBuffer = new LinkedList<>();
        StringBuilder concatenatedPackageInput = new StringBuilder();
        for (String i : packageInput) {
            concatenatedPackageInput.append(i);
            concatenatedPackageInput.append(" ");
        }
        String[] splittedInput = concatenatedPackageInput.toString().split(";");
        for (String i : splittedInput) {
            commandsBuffer.offer(parseCommand(i));
        }
    }

    @Override
    public String[] getNext() throws NoNextCommand {
        if (commandsBuffer.isEmpty()) {
            throw new NoNextCommand();
        }
        return commandsBuffer.remove();
    }

    @Override
    public boolean hasNext() {
        return !commandsBuffer.isEmpty();
    }

}
