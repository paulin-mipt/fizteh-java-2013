package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandStartHTTP extends Command {

    public boolean exec(String[] args, State curState) throws IOException {
        ServletState myState = ServletState.class.cast(curState);
        if (args.length > 1) {
            throw new IllegalArgumentException("Illegal arguments");
        }

        try {
            if (args.length == 0) {
                myState.server.start(-1);
            } else {
                int port = Integer.parseInt(args[0]);
                myState.server.start(port);
            }
            System.out.println(String.format("started at " + myState.server.getPortNumber()));
        } catch (Exception e) {
            System.out.println("not started: " + e.getMessage());
            return false;
        }
        return true;
    }

    public String getCmd() {
        return "starthttp";
    }
}
