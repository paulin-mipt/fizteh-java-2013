package ru.fizteh.fivt.students.belousova.utils;

public class TruePredicate<T> implements Predicate<T> {
    public static <T> TruePredicate<T> create() {
        return new TruePredicate<T>();
    }

    @Override
    public boolean apply(T input) {
        return true;
    }
}
