package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.util.*;
import java.io.*;

public class Launcher {
      private Map<String, Command> commandsMap;

      public Launcher() {
            commandsMap = new HashMap<String, Command>();
      }

      public void addCommand(Command c) {
            commandsMap.put(c.getName(), c);
      }

      public boolean execute(String[] args, State state) throws IOException, ShellException, IllegalFormatException {
            if (args.length == 0) {
                  return true;
            }
            Command com = commandsMap.get(args[0]);
            if (com == null) {
                  throw new IOException(args[0] + ": нет такой комманды!");
            }
            if (((com.getArgNumber() + 1) != args.length && (com.getArgNumber() != -1))
                        || (args.length == 1 && (com.getArgNumber() == -1))) {
                  throw new IOException(args[0] + ": неверное количество аргументов");
            }
            com.execute(args, state);

            if (args[0].equals("exit")) {
                  return false;
            } else
                  return true;
      }
}