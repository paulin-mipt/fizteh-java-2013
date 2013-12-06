package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class GetCommand implements Command {

      private String name = "get";
      private final int argNumber = 1;

      MultyFileMapTableProvider provider;

      public GetCommand(MultyFileMapTableProvider provider) {
            this.provider = provider;
      }

      public void execute(String[] args, State state) {
            if (provider.currentDataBase == null) {
                  throw new IllegalArgumentException("Таблица не выбрана");
            }
            String value = provider.currentDataBase.get(args[1]);
            if (value == null) {
                  System.out.println("not found");
            } else {
                  System.out.println("found" + "\n" + value);
            }
      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }
}
