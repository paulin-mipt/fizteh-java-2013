package ru.fizteh.fivt.students.eltyshev.multifilemap;

public class DatabaseFileDescriptor {
    public int bucket;
    public int file;

    public DatabaseFileDescriptor(int bucket, int file) {
        this.bucket = bucket;
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseFileDescriptor that = (DatabaseFileDescriptor) o;

        if (bucket != that.bucket) return false;
        if (file != that.file) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bucket;
        result = 31 * result + file;
        return result;
    }
}
