package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.utils.Predicate;

import java.nio.charset.StandardCharsets;

public class IsKeyValid implements Predicate<String> {
    private int nfile;
    private int ndirectory;

    public IsKeyValid(int nf, int nd) {
        nfile = nf;
        ndirectory = nd;
    }

    @Override
    public boolean apply(String input) {
        int keyByte = Math.abs(input.getBytes(StandardCharsets.UTF_8)[0]);
        int nd = keyByte % 16;
        int nf = keyByte / 16 % 16;
        return (nd == ndirectory) && (nf == nfile);
    }
}
