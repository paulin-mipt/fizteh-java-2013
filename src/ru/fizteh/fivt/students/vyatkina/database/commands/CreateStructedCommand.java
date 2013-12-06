package ru.fizteh.fivt.students.vyatkina.database.commands;

import ru.fizteh.fivt.students.vyatkina.CommandExecutionException;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateStructedCommand extends DatabaseCommand {

    public CreateStructedCommand(DatabaseState state) {
        super(state);
        this.name = "create";
        this.argsCount = 2;
    }

    @Override
    public String[] parseArgs(String signature) {
        Pattern ARGS_PATTERN = Pattern.compile("^(.*)\\s+\\((.*)\\)$");
        Matcher matcher = ARGS_PATTERN.matcher(signature);
        if (matcher.matches()) {
            String[] args = new String[2];
            args[0] = matcher.group(1);
            args[1] = matcher.group(2);
            return args;
        } else {
            throw new IllegalArgumentException(WRONG_NUMBER_OF_ARGUMENTS);
        }
    }

    @Override
    public void execute(String[] args) {
        String tableName = args[0];
        String structuredSignature = args[1];
        boolean newTableIsCreated;
        try {
            newTableIsCreated = state.databaseAdapter.createTable(tableName, structuredSignature);
        }
        catch (UnsupportedOperationException | IllegalArgumentException e) {
            state.printErrorMessage(e.getMessage());
            return;
        }
        catch (WrappedIOException e) {
            throw new CommandExecutionException(e.getMessage());
        }
        if (newTableIsCreated) {
            state.printUserMessage("created");
        } else {
            state.printUserMessage(tableName + " exists");
        }
    }

}
