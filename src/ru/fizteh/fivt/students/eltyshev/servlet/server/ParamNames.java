package ru.fizteh.fivt.students.eltyshev.servlet.server;

public enum ParamNames {
    TABLE_NAME("table"),
    TRANSACTION_ID("tid"),
    KEY("key"),
    VALUE("value"),
    DIFF("diff");

    public String name;

    private ParamNames(String name) {
        this.name = name;
    }
}