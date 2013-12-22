package ru.fizteh.fivt.students.vlmazlov.storeable;

public class StoreableDataBaseState {
    private StoreableTable activeTable;
    private final StoreableTableProvider provider;

    public StoreableDataBaseState(StoreableTableProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException();
        }

        this.provider = provider;
    }

    public StoreableTable getActiveTable() {
        return activeTable;
    }

    public void setActiveTable(StoreableTable newActiveTable) {
        activeTable = newActiveTable;
    }

    public StoreableTableProvider getProvider() {
        return provider;
    }
}
