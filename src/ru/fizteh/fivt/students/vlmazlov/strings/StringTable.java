package ru.fizteh.fivt.students.vlmazlov.strings;

import ru.fizteh.fivt.storage.strings.Table;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.students.vlmazlov.utils.ProviderWriter;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.utils.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.utils.TableReader;
import ru.fizteh.fivt.students.vlmazlov.utils.TableWriter;
import ru.fizteh.fivt.students.vlmazlov.generics.GenericTable; 

public class StringTable extends GenericTable<String> implements DiffCountingTable, Cloneable {

	private StringTableProvider provider;
	private ReadWriteLock getCommitLock;

	public StringTable(StringTableProvider provider, String name) {
		super(name);
		this.provider = provider;

		getCommitLock = new ReentrantReadWriteLock();
	}

	public StringTable(StringTableProvider provider, String name, boolean autoCommit) {
		super(name, autoCommit);
		this.provider = provider;

		getCommitLock = new ReentrantReadWriteLock();
	}

	@Override
	protected String getCommited(String key) {
		return commited.get(key);
	}

	@Override
    protected void readLock(){
    	getCommitLock.readLock().lock();
    }
    
    @Override
    protected void readUnLock() {
    	getCommitLock.readLock().unlock();
    }
    
    @Override
    protected void writeLock() {
    	getCommitLock.writeLock().lock();
    }
    
    @Override
    protected void writeUnLock() {
    	getCommitLock.writeLock().unlock();
    }

	public void read(String root, String fileName) 
	throws IOException, ValidityCheckFailedException {
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		if (fileName == null) {
			throw new FileNotFoundException("File not specified");
		}
 
		TableReader.readTable(new File(root), new File(root, fileName), this, provider);
	}

	public void write(String root, String fileName)
	throws IOException, ValidityCheckFailedException {
		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		if (fileName == null) {
			throw new FileNotFoundException("File not specified");
		} 

		TableWriter.writeTable(new File(root), new File(root, fileName), this, provider);
	}

	@Override
	public int commit() {
		try {
			return super.commit();
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public StringTable clone() {
        return new StringTable(provider, getName(), autoCommit);
    }

    @Override
    public void checkRoot(File root) throws ValidityCheckFailedException {
    	ValidityChecker.checkMultiTableRoot(root);
    }

    @Override
    protected void storeOnCommit() throws IOException, ValidityCheckFailedException {
    	ProviderWriter.writeMultiTable(this, new File(provider.getRoot(), getName()), provider);
    }

    public int size() {
    	int size = 0;
        readLock();

        try {
            size = commited.size();

            for (Map.Entry<String, String> entry : changed.get().entrySet()) {
                if (getCommited(entry.getKey()) == null) {
                    ++size;
                }
            }

            for (String entry : deleted.get()) {
                if (getCommited(entry) != null) {
                    --size;
                }
            }
        } finally {
            readUnLock();
        }

        return size;

    }
}