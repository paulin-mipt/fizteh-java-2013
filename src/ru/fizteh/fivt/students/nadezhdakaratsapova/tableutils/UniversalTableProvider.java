package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;


import java.io.IOException;

public interface UniversalTableProvider {

    UniversalDataTable getTable(String name);

    void removeTable(String name) throws IOException;

    UniversalDataTable getCurTable();

    UniversalDataTable setCurTable(String newTable) throws IOException;

}
