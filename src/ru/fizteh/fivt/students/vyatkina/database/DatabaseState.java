package ru.fizteh.fivt.students.vyatkina.database;


import ru.fizteh.fivt.students.vyatkina.State;

public class DatabaseState extends State {

    public DatabaseAdapter databaseAdapter;

    public DatabaseState(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }


}
