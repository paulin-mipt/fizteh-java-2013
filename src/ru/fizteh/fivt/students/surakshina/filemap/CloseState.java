package ru.fizteh.fivt.students.surakshina.filemap;

public class CloseState {
    volatile boolean isClosed = false;

    public CloseState() {
    }

    public void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("It has closed already");
        }
    }

    public void setClose() {
        isClosed = true;
    }

    public boolean isClose() {
        return isClosed;
    }

}
