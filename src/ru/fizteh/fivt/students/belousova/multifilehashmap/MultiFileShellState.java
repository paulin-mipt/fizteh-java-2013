package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.filemap.TableState;

public class MultiFileShellState extends TableState {
    private ChangesCountingTableProvider tableProvider = null;

    public MultiFileShellState() {
    }

    public MultiFileShellState(ChangesCountingTableProvider provider, ChangesCountingTable table) {
        tableProvider = provider;
        currentTable = table;
    }

    public boolean getTable(String name) {
        return (tableProvider.getTable(name) != null);
    }

    public boolean createTable(String name) {
        return (tableProvider.createTable(name) != null);
    }

    public void removeTable(String name) {
        tableProvider.removeTable(name);
    }

    public void setCurrentTable(String name) {
        currentTable = tableProvider.getTable(name);
    }

    public void resetCurrentTable() {
        currentTable = null;
    }
}