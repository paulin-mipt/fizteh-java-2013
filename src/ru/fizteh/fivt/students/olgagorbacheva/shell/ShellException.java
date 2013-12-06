package ru.fizteh.fivt.students.olgagorbacheva.shell;

import java.io.IOException;

public class ShellException extends IOException {
      /**
       * 
       */
      private static final long serialVersionUID = -103335737416020285L;
      /**
       * 
       */
      private String message;
      
      public ShellException(String _message) {
            message = _message;
      }
      
      public String getLocalizedMessage() {
            return message;
      }
}