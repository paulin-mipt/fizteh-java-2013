package ru.fizteh.fivt.students.vlmazlov.generics;

public class DataBaseState<V, T extends GenericTable<V>> {
    private GenericTable<V> activeTable;
    private final GenericTableProvider<V, T> provider;

    protected DataBaseState() {
        provider = null;
    }

    public DataBaseState(GenericTableProvider<V, T> provider) {
        if (provider == null) {
            throw new IllegalArgumentException();
        }

        this.provider = provider;
    }

    public GenericTable<V> getActiveTable() {
        return activeTable;
    }

    public void setActiveTable(GenericTable<V> newActiveTable) {
        activeTable = newActiveTable;
    }

    public GenericTableProvider<V, T> getProvider() {
        return provider;
    }
}
