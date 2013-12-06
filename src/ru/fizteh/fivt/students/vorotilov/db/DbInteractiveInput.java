package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.students.vorotilov.shell.InteractiveInput;

public class DbInteractiveInput extends InteractiveInput {

    public DbInteractiveInput() {
        super();
    }

    @Override
    protected String[] parseCommand(String input) {
        String trimmedInput = input.trim();
        String[] autoParsed = trimmedInput.split("\\s+");
        if (autoParsed.length > 2) {
            String[] parsedInput = new String[3];
            parsedInput[0] = autoParsed[0];
            parsedInput[1] = autoParsed[1];
            StringBuilder tempValue = new StringBuilder(input);
            tempValue.delete(0, autoParsed[0].length());
            tempValue = new StringBuilder(tempValue.toString().trim());
            tempValue.delete(0, autoParsed[1].length());
            parsedInput[2] = tempValue.toString().trim();
            return parsedInput;
        } else {
            return autoParsed;
        }
    }

}
