package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.FileNotFoundException;
import java.io.IOException;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Shell;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;
import ru.fizteh.fivt.students.olgagorbacheva.shell.ExitCommand;

public class Main {

      /**
       * @param args
       */
      public static void main(String[] args) {
            if (System.getProperty("fizteh.db.dir") == null) {
                  System.exit(1);
            }
            String dir = System.getProperty("fizteh.db.dir");
            Shell multyFileHashMap = new Shell(new State(System.getProperty("fizteh.db.dir")));
            MultyFileMapTableProviderFactory providerFactory = new MultyFileMapTableProviderFactory();
            MultyFileMapTableProvider provider;
            try {
                  provider = providerFactory.create(dir);
                  multyFileHashMap.addCommand(new RemoveCommand(provider));
                  multyFileHashMap.addCommand(new RollbackCommand(provider));
                  multyFileHashMap.addCommand(new GetCommand(provider));
                  multyFileHashMap.addCommand(new CommitCommand(provider));
                  multyFileHashMap.addCommand(new CreateCommand(provider));
                  multyFileHashMap.addCommand(new DropCommand(provider));
                  multyFileHashMap.addCommand(new PutCommand(provider));
                  multyFileHashMap.addCommand(new SizeCommand(provider));
                  multyFileHashMap.addCommand(new UseCommand(provider));
                  multyFileHashMap.addCommand(new ExitCommand());

                  multyFileHashMap.execute(args);

                  provider.writeToFile();
            } catch (IllegalArgumentException e) {
                  System.err.println("Я выпал в Main");
                  System.err.println(e.getMessage());
                  System.exit(1);
            } catch (FileNotFoundException e) {
                  System.err.println("Я без понятия, как это произошло, тем более на данном этапе... Но файл не найден, ищите партизанов, затирающих Ваши файлы во время работы программы.");
                  System.exit(1);
            } catch (IOException e) {
                  System.err.println(e.getMessage());
                  System.exit(1);
            }
      }

}
