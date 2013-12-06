package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class FileMapState implements State {
    private File curFile;

    public void setCurState(File file) {
        curFile = file;
    }

    public File getCurState() {
        return curFile;
    }

    public boolean usingTables() {
        return false;
    }

    @Override
    public String get(String key) {
        return AbstractFileMap.getContent().get(key);
    }

    @Override
    public String put(String key, String value) throws ParseException {
        return AbstractFileMap.getContent().put(key, value);
    }

    @Override
    public String remove(String key) {
        return AbstractFileMap.getContent().remove(key);
    }

    @Override
    public void saveTable(Object table) throws IOException {
        AbstractFileMap.commitFile(
                AbstractFileMap.getFile(), AbstractFileMap.getContent().keySet(), AbstractFileMap.getContent()
        );

        AbstractFileMap.getFile().close();
    }

    @Override
    public File getCurDir() {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public Object getCurTable() {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public void createTable(String curTableName) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public void setCurTable(String curTableName) {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public void removeTable(String curTableName) throws IOException {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public void readTableOff(Object table) throws IOException, ParseException {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public int commit() {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public int rollback() {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public int getDiffSize() {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public File getCurTableDir() {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }

    @Override
    public void clearContentAndDiff() {
        throw new UnsupportedOperationException("ERROR: this function is not supported here");
    }
}
