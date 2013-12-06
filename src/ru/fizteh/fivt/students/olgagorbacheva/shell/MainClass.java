package ru.fizteh.fivt.students.olgagorbacheva.shell;

public class MainClass {

      /**
       * @param args
       */
      public static void main(String[] args) {
            
            Shell sh = new Shell(new State());
            
            Command cd = new ChangeDirectoryCommand();
            sh.addCommand(cd);
            Command mkdir = new MakeDirectoryCommand();
            sh.addCommand(mkdir);
            Command pwd = new PrintWorkingDirectoryCommand();
            sh.addCommand(pwd);
            Command rm = new RemoveCommand();
            sh.addCommand(rm);
            Command cp = new CopyCommand();
            sh.addCommand(cp);
            Command mv = new MoveCommand();
            sh.addCommand(mv);
            Command dir = new DirectoryCommand();
            sh.addCommand(dir);
            Command exit = new ExitCommand();
            sh.addCommand(exit);
            
            sh.execute(args);
      }

}