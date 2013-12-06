package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class CreateCommand implements Command {

      private String name = "create";
      private final int argNumber = 1;

      MultyFileMapTableProvider provider;

      public CreateCommand(MultyFileMapTableProvider provider) {
            this.provider = provider;
      }

      public void execute(String[] args, State state) {
            if (provider.createTable(args[1]) == null) {
                  System.out.println(args[1] + " exists");
            } else {
                  System.out.println("created");
            }
      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }
}