package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.values;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container.TableValueUnpacker;

public class TableValueUnpackerString implements TableValueUnpacker<String> {
    @Override
    public String getValueFromString(String value) throws Exception {
        return value;
    }
}
