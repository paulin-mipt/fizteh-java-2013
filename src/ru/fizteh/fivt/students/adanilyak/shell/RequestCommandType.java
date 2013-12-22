
package ru.fizteh.fivt.students.adanilyak.shell;

public enum RequestCommandType {

    cd("cd"),
    mkdir("mkdir"),
    pwd("pwd"),
    rm("rm"),
    cp("cp"),
    mv("mv"),
    dir("dir"),
    exit("exit");

    private String typeValue;

    private RequestCommandType(String type) {
        typeValue = type;
    }

    public static RequestCommandType getType(String pType) {
        for (RequestCommandType type : RequestCommandType.values()) {
            if (type.getTypeValue().equals(pType)) {
                return type;
            }
        }
        throw new RuntimeException("Unknown command");
    }

    public String getTypeValue() {
        return typeValue;
    }

}
