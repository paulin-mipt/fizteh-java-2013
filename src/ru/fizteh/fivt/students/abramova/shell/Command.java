package ru.fizteh.fivt.students.abramova.shell;

import java.io.IOException;
import java.util.Map;
import ru.fizteh.fivt.students.abramova.filemap.*;

public abstract class Command {
    public final String name;

    public Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //Возвращает true, если комманда работает с файлом Map
    public  boolean isMapping() {
        return false;
    }

    //Если работа команды прошла успешно возвращает 0, иначе отличное от нуля значение
    abstract public  int doCommand(String[] args, Status status) throws IOException;

    //Возвращает true, если передано верное количество аргументов
    abstract public  boolean correctArgs(String[] args);
}