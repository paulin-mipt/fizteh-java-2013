package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.IOException;

import ru.fizteh.fivt.students.olgagorbacheva.shell.State;
import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;

public class UseCommand implements Command {

      private String name = "use";
      private final int argNumber = 1;

      MultyFileMapTableProvider provider;

      public UseCommand(MultyFileMapTableProvider provider) {
            this.provider = provider;
      }

      public void execute(String[] args, State state) throws IOException {
            if (provider.currentDataBase != null && provider.currentDataBase.sizeChangesCommit() != 0) {
                  throw new IOException(provider.currentDataBase.sizeChangesCommit() + " unsaved changes");
            }
            if (provider.setTable(args[1]) == null) {
                  System.out.println(args[1] + " not exist");
            } else {
                  if (provider.currentDataBase.size() == 0) {
                        provider.currentDataBase.readFile();
                  }
                  System.out.println("using " + args[1]);
            }

      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}
