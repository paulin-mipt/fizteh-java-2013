package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class RemoveCommand implements Command {

      private String name = "remove";
      private final int argNumber = 1;

      MultyFileMapTableProvider provider;

      public RemoveCommand(MultyFileMapTableProvider provider) {
            this.provider = provider;
      }

      public void execute(String[] args, State state) {
            if (provider.currentDataBase == null) {
                  throw new IllegalArgumentException("Таблица не выбрана");
            }
            if (provider.currentDataBase.remove(args[1]) == null) {
                  System.out.println("not found");
            } else {
                  System.out.println("removed");
            }

      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}
