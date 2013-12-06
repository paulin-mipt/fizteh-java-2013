package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.extend;

import ru.fizteh.fivt.storage.strings.TableProvider;

public interface ExtendProvider extends TableProvider {
   
    @Override
    ExtendTable getTable(String name);
    
    @Override
    ExtendTable createTable(String name);

}
