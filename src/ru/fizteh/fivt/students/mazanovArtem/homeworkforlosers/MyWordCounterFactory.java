package ru.fizteh.fivt.students.mazanovArtem.homeworkforlosers;


import ru.fizteh.fivt.file.WordCounterFactory;

public class MyWordCounterFactory implements WordCounterFactory {

    public MyWordCounter create() {
        return new MyWordCounter();
    }

}
