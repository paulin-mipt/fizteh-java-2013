package ru.fizteh.fivt.students.surakshina.filemap;

public class ValueState<ValueType> {
    private ValueType commitedValue;
    private ValueType value;

    public ValueState(ValueType commitedValue, ValueType value) {
        this.commitedValue = commitedValue;
        this.value = value;
    }

    public boolean needToCommit() {
        if (commitedValue == null) {
            return (value != null);
        } else {
            return !commitedValue.equals(value);
        }
    }

    public ValueType getValue() {
        return value;
    }

    public ValueType getCommitedValue() {
        return commitedValue;
    }

    public void setValue(ValueType value) {
        this.value = value;
    }

    public boolean commitValue() {
        if (this.needToCommit()) {
            this.commitedValue = value;
            return true;
        } else {
            return false;
        }
    }

    public boolean rollbackValue() {
        if (this.needToCommit()) {
            this.value = commitedValue;
            return true;
        } else {
            return false;
        }
    }
}
