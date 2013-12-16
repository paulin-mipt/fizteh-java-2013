package ru.fizteh.fivt.students.eltyshev.servlet.server;

public enum CommandNames {
    BEGIN("/begin"),
    COMMIT("/commit"),
    ROLLBACK("/rollback"),
    GET("/get"),
    PUT("/put"),
    SIZE("/size"),
    TEST("/test");

    public String name;

    private CommandNames(String name) {
        this.name = name;
    }
}
