package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class PutCommand implements Command {

      private String name = "put";
      private final int argNumber = 2;

      MultyFileMapTableProvider provider;

      public PutCommand(MultyFileMapTableProvider provider) {
            this.provider = provider;
      }

      public void execute(String[] args, State state) throws IllegalArgumentException {
            if (provider.currentDataBase == null) {
                  throw new IllegalArgumentException("Таблица не выбрана");
            }
            String value = provider.currentDataBase.put(args[1], args[2]);
            if (value == null) {
                  System.out.println("new");
            } else {
                  System.out.println("overwrite" + "\n" + value);
            }
      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}
