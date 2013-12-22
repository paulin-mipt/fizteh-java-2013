package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandUse extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        try {
            MultiDBState myState = MultiDBState.class.cast(curState);
            if (args.length != 1) {
                throw new IllegalArgumentException("Illegal arguments");
            }
            String path = myState.getProperty(myState);
            if (!myState.database.tables.containsKey(args[0])) {
                System.out.println(args[0] + " not exists");
                return false;
            }
            if (myState.table != null) {
                TableBuilder tableBuilder = new TableBuilder(myState.table.provider, myState.table);
                myState.table.save(tableBuilder);
            }
            if (myState.table != null && myState.table.getUncommittedChangesCount() != 0) {
                System.out.println(myState.table.getUncommittedChangesCount() + " unsaved changes");
                return false;
            }
            myState.table = myState.database.getTable(args[0]);
            System.out.println("using " + args[0]);
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IllegalStateException f) {
            return false;
        }
        return true;
    }

    public String getCmd() {
        return "use";
    }
}
