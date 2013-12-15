package ru.fizteh.fivt.students.dubovpavel.executor;

import java.io.PrintStream;
import java.util.ArrayList;

public class Dispatcher {
    private Parser parser;
    private int invalidSequences;
    private int invalidOperations;
    private ArrayList<Performer> performers;
    private boolean forwarding;
    protected boolean shutdown;
    private Boolean quiet;

    public static class DispatcherException extends Exception {
        public DispatcherException(String msg) {
            super(msg);
        }
    }

    public enum MessageType {
        SUCCESS,
        WARNING,
        ERROR
    }

    public void setQuiet(boolean quiet) {
        synchronized (this.quiet) {
            this.quiet = quiet;
        }
    }
    public String getInitProperty(String key) throws DispatcherException {
        String value = System.getProperty(key);
        if (value == null) {
            shutdown = true;
            throw new DispatcherException(
                    callbackWriter(MessageType.ERROR, String.format("'%s' property is null", key)));
        } else {
            return value;
        }
    }

    public Dispatcher(boolean forwarding) {
        invalidSequences = 0;
        parser = new Parser();
        this.forwarding = forwarding;
        performers = new ArrayList<>();
        shutdown = false;
        quiet = false;
    }

    public void addPerformer(Performer performer) {
        performers.add(performer);
    }

    public String callbackWriter(MessageType type, String msg) {
        synchronized (quiet) {
            if (!quiet) {
                PrintStream stream;
                if (type == MessageType.SUCCESS || type == MessageType.WARNING) {
                    stream = System.out;
                } else {
                    stream = System.err;
                }
                stream.println(msg);
                return msg;
            } else {
                return null;
            }
        }
    }

    public void shutDown() {
        shutdown = true;
    }

    public boolean online() {
        return !shutdown;
    }

    public void sortOut(String commandSequence) throws DispatcherException {
        try {
            ArrayList<Command> commands = parser.getCommands(this, commandSequence);
            for (Command command : commands) {
                try {
                    if (shutdown) {
                        break;
                    }
                    boolean performed = false;
                    for (Performer performer : performers) {
                        if (performer.pertains(command)) {
                            performer.execute(this, command);
                            performed = true;
                            break;
                        }
                    }
                    if (!performed) {
                        callbackWriter(MessageType.ERROR, String.format("%s is not correct", command.getDescription()));
                    }
                } catch (PerformerException e) {
                    invalidOperations++;
                    if (forwarding) {
                        throw new DispatcherException(e.getMessage());
                    }
                }
            }
        } catch (Parser.IncorrectSyntaxException e) {
            invalidSequences++;
            if (forwarding) {
                throw new DispatcherException(e.getMessage());
            }
        }
    }
}
