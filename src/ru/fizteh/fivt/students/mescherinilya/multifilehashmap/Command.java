package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public interface Command {

    String getName();

    int getArgsCount();

    void execute(String[] args) throws Exception;


}
