package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

class Distribution<L, R> { // God gave us a pair. Look at Adam and Eve. Why don't we use this advanced technology? :-(
    private L l;
    private R r;

    public Distribution(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public L getDir() {
        return this.l;
    }

    public R getChunk() {
        return this.r;
    }
}
