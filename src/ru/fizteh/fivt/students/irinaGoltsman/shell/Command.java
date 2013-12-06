package ru.fizteh.fivt.students.irinaGoltsman.shell;

public interface Command {
    //Получить название команды
    String getName();

    //Получить количество аргументов команды.
    int getCountOfArguments();

    //Проверить аргументы.
    boolean check(String[] parts);

    //Выполнить команду.
    Code perform(String[] parts);
}
