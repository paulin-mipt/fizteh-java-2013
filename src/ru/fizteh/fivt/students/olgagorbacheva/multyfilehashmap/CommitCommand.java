package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class CommitCommand implements Command {

      private String name = "commit";
      private final int argNumber = 0;

      MultyFileMapTableProvider provider;

      public CommitCommand(MultyFileMapTableProvider provider) {
            this.provider = provider;
      }

      public void execute(String[] args, State state) {
            if (provider.currentDataBase == null) {
                  throw new IllegalArgumentException("Таблица не выбрана");
            }
            System.out.println(provider.currentDataBase.commit());
      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }
}