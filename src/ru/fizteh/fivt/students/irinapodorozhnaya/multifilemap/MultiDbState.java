package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.students.irinapodorozhnaya.db.DbState;

public interface MultiDbState extends DbState {

    int getCurrentTableSize() throws IOException;

    int rollBack() throws IOException;

    void drop(String name) throws IOException;

    void create(String name, List<Class<?>> types) throws IOException;
    
    void use(String name) throws IOException;

}
