package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.values;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container.TableValuePacker;

public class TableValuePackerString implements TableValuePacker<String> {
    @Override
    public String getValueString(String value) throws Exception {
        return value;
    }
}
