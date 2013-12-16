package ru.fizteh.fivt.students.eltyshev.servlet.shell;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

import java.io.IOException;
import java.util.List;

public class StopCommand extends AbstractCommand<ServletShellState> {
    public StopCommand() {
        super("stophttp", "stophttp");
    }

    @Override
    public void executeCommand(String params, ServletShellState shellState) throws IOException {
        List<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 0) {
            throw new IllegalArgumentException("too many arguments");
        }

        try {
            String message = String.format("stopped at %d", shellState.server.getPort());
            shellState.server.stop();
            System.out.println(message);
        } catch (IOException | IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
