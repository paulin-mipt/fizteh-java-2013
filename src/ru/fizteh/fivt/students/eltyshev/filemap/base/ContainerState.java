package ru.fizteh.fivt.students.eltyshev.filemap.base;

public enum ContainerState {
    NOT_INITIALIZED {
        @Override
        public void checkOperationsAllowed() {
            throw new IllegalStateException("not initialized yet");
        }
    },
    WORKING {
        @Override
        public void checkOperationsAllowed() {
            // everything is fine
        }
    },
    CLOSED {
        @Override
        public void checkOperationsAllowed() {
            throw new IllegalStateException("already closed");
        }
    };

    public abstract void checkOperationsAllowed();
}
