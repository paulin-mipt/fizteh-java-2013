package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class DropCommand implements Command {

      private String name = "drop";
      private final int argNumber = 1;

      MultyFileMapTableProvider provider;

      public DropCommand(MultyFileMapTableProvider provider) {
            this.provider = provider;
      }

      public void execute(String[] args, State state) {
            if (provider.getTable(args[1]) == null) {
                  System.out.println("tablename not exists");
            } else {
                  if (provider.currentDataBase != null) {
                        if (args[1].equals(provider.currentDataBase.getName())) {
                              provider.setTable(null);
                        }
                  }
                  provider.removeTable(args[1]);
                  System.out.println("droped");
            }

      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}
