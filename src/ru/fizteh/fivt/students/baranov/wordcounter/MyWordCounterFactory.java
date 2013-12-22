package ru.fizteh.fivt.students.baranov.wordcounter;

import ru.fizteh.fivt.file.WordCounterFactory;

public class MyWordCounterFactory implements WordCounterFactory {
    public MyWordCounterFactory() {
    }

    public MyWordCounter create() {
        return new MyWordCounter();
    }
}

