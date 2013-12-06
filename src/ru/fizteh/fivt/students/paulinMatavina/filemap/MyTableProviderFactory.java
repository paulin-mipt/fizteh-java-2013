package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.IOException;
import java.util.HashSet;

import ru.fizteh.fivt.storage.structured.*;

public class MyTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private HashSet<MyTableProvider> providerSet = new HashSet<MyTableProvider>();
    private volatile boolean isClosed;
    
    public MyTableProviderFactory() {
        isClosed = false;
    }
    
    public TableProvider create(String dir) throws IOException {
        checkClosed();
        MyTableProvider newProvider = new MyTableProvider(dir);
        providerSet.add(newProvider);
        return newProvider;
    }

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("TableProviderFactory was closed");
        }
    }
    
    @Override
    public void close() throws Exception {
        isClosed = true;
        
        for (MyTableProvider provider : providerSet) {
            provider.close();
        }
    }   
}
