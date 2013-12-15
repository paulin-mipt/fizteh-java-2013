package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapStringShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.Shell;

public class MultiFileMapStringShellState extends FileMapStringShellState
        implements MultiFileMapShellState<Table, String, String> {
    public TableProvider tableProvider;

    @Override
    public Table createTable(String tableName) {
        return tableProvider.createTable(tableName);
    }

    @Override
    public void dropTable(String tableName) {
        tableProvider.removeTable(tableName);
    }

    @Override
    public Table useTable(String tableName) {
        table = tableProvider.getTable(tableName);
        return table;
    }

    @Override
    public String getCurrentTableName() {
        return table.getName();
    }

    @Override
    public String[] parseCreateCommand(String argumentsLine) {
        return Shell.parseCommandParameters(argumentsLine);
    }
}
