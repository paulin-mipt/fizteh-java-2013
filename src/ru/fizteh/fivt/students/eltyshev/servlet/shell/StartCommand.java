package ru.fizteh.fivt.students.eltyshev.servlet.shell;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

import java.io.IOException;
import java.util.List;

public class StartCommand extends AbstractCommand<ServletShellState> {
    public StartCommand() {
        super("starthttp", "starthttp [<port>]");
    }

    @Override
    public void executeCommand(String params, ServletShellState shellState) throws IOException {
        List<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }

        try {
            if (parameters.size() == 0) {
                shellState.server.start(-1);
            } else {
                int port = Integer.parseInt(parameters.get(0));
                shellState.server.start(port);
            }
            System.out.println(String.format("started at %d", shellState.server.getPort()));
        } catch (Exception e) {
            System.out.println("not started: " + e.getMessage());
        }
    }
}
