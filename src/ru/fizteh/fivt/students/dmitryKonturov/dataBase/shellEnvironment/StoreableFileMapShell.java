package ru.fizteh.fivt.students.dmitryKonturov.dataBase.shellEnvironment;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableImplementation;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableProviderFactoryImplementation;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellCommand;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellInfo;

import java.io.IOException;
import java.nio.file.Path;

public class StoreableFileMapShell extends ShellEmulator {

    private static StoreableShellInfo getStoreableShellInfo(Path workspace) throws IOException {
        TableProviderFactory providerFactory = new TableProviderFactoryImplementation();
        TableProvider tableProvider = providerFactory.create(workspace.toString());
        return new StoreableShellInfo(tableProvider);
    }

    public StoreableFileMapShell(Path workspace) throws IOException {
        super(getStoreableShellInfo(workspace));
        super.addToCommandList(WorkWithChosenTableCommands.getPackageCommands());
        super.addToCommandList(WorkWithTableProviderCommands.getPackageCommands());
        super.addToCommandList(new ShellCommand[]{new ExitCommand()});
    }

    /*@Override
    public String getGreetingString() {
        return "$ " + System.lineSeparator();
    } */

    @Override
    protected String[] shellParseArguments(String bigArg) {
        String newBigArg = bigArg.trim();
        String[] args;
        if (newBigArg.length() == 0) {
            args = new String[0];
        } else {
            args = new String[1];
            args[0] = newBigArg;
        }
        return args;
    }

    static class ExitCommand implements ShellCommand {
        @Override
        public String getName() {
            return "exit";
        }

        @Override
        public void execute(String[] args, ShellInfo info) throws ShellException {
            if (args.length != 0) {
                throw new ShellException(getName(), "Bad arguments");
            }
            TableImplementation table = (TableImplementation) info.getProperty("CurrentTable");
            if (table != null) {
                try {
                    int unsavedChanges = table.getUnsavedChangesCount();
                    if (unsavedChanges > 0) {
                        table.commit();
                    }
                } catch (Exception e) {
                    throw new ShellException(getName(), e);
                }
            }
            System.exit(0);
        }
    }

    @Override
    public void packageMode(String query) throws ShellException {
        super.packageMode(query + " exit");
    }

}
