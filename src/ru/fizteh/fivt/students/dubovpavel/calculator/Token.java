package ru.fizteh.fivt.students.dubovpavel.calculator;

class Token<L, R> {
    private L l;
    private R r;

    public Token(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public L getLexem() {
        return this.l;
    }

    public R getPointer() {
        return this.r;
    }
}
