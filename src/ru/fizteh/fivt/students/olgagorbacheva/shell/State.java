package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class State {
      private Path state;
      
      public State() {
            state = Paths.get(new File(System.getProperty("fizteh.db.dir")).getAbsolutePath());
      }
      
      public State(String st) {
            state = Paths.get(new File(System.getProperty("fizteh.db.dir"), st).getAbsolutePath());
      }
      
      public State(Path st) {
            state = st;
      }

      public String getState() {
            return state.toString();
      }
      
      public void setState(Path st) {
            state = st;
      }
      
      public void setState(String st) {
            state = Paths.get(st);
      }
      
}