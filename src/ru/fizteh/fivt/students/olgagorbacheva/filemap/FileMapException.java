package ru.fizteh.fivt.students.olgagorbacheva.filemap;

public class FileMapException extends Exception {

      private static final long serialVersionUID = -8333296962044558961L;

      private String message;

      public FileMapException(String message) {
            this.message = message;
      }

      public String getLocalizedMessage() {
            return message;
      }

}