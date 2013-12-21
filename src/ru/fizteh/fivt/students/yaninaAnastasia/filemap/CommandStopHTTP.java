package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandStopHTTP extends Command {

    public boolean exec(String[] args, State curState) throws IOException {
        ServletState myState = ServletState.class.cast(curState);
        if (args.length > 0) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        if (!myState.server.isStarted()) {
            System.out.println("not started");
            return true;
        }

        try {
            String stopText = "stopped at " + myState.server.getPortNumber();
            myState.server.stop();
            System.out.println(stopText);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public String getCmd() {
        return "stophttp";
    }
}
