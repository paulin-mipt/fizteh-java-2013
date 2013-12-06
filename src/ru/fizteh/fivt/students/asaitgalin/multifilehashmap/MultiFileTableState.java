package ru.fizteh.fivt.students.asaitgalin.multifilehashmap;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions.ChangesCountingTable;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions.ChangesCountingTableProvider;

public class MultiFileTableState {
    public ChangesCountingTableProvider provider;
    public ChangesCountingTable currentTable;
}
