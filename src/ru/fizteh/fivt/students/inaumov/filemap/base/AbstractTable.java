package ru.fizteh.fivt.students.inaumov.filemap.base;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.inaumov.filemap.handlers.ReadHandler;
import ru.fizteh.fivt.students.inaumov.filemap.handlers.WriteHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTable implements Table {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

	protected HashMap<String, String> tableHash = new HashMap<String, String>();
	protected HashMap<String, OldAndNewValue> modifiedTableHash = new HashMap<String,  OldAndNewValue>();

    private class OldAndNewValue {
        public String oldValue;
        public String newValue;

        OldAndNewValue(String oldValue, String newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }

	final private String tableName;
	private String dir;
	
	private int tableSize = 0;
	private int unsavedChangesNumber = 0;
	
	public WriteHandler writeHandler;
	public ReadHandler readHandler;

	public abstract void loadTable() throws IOException;

	public abstract void saveTable() throws IOException;
	
	public AbstractTable(String dir, String tableName) throws IOException, IllegalArgumentException {
		if (dir == null || dir.isEmpty()) {
			throw new IllegalArgumentException("directory can't be null or empty");
		}
		if (tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("table name can't be null or empty");
		}
		
		this.tableName = tableName;
		this.dir = dir;
		
		loadTable();
	}
	
	public String getName() {
		return tableName;
	}

	public String getDir() {
		return dir;
	}
	
	public String get(String key) throws IllegalArgumentException {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("key can't be null or empty");
		}
		
		if (modifiedTableHash.containsKey(key)) {
			return modifiedTableHash.get(key).newValue;
		}

		return tableHash.get(key);
	}

	public String put(String key, String value) throws IllegalArgumentException {
		if (key == null || key.isEmpty() || key.trim().isEmpty()) {
			throw new IllegalArgumentException("key can't be null or empty");
		}
		if (value == null || value.isEmpty() || value.trim().isEmpty()) {
			throw new IllegalArgumentException("value can't be null or empty");
		}
		
		String oldValue = getOldValue(key);
        if (oldValue == null) {
            tableSize += 1;
        }

        makeChange(key, value);

        unsavedChangesNumber += 1;

		return oldValue;
	}

	public String remove(String key) throws IllegalArgumentException {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("key can't be null or empty");
		}
		
		if (get(key) == null) {
            return null;
        }

        String oldValue = getOldValue(key);
        makeChange(key, null);

        if (oldValue != null) {
            tableSize -= 1;
        }

        unsavedChangesNumber += 1;

		return oldValue;
	}

	public int size() {
		return tableSize;
	}

	public int getUnsavedChangesNumber() {
		return unsavedChangesNumber;
	}

    private void makeChange(String key, String value) {
        if (modifiedTableHash.containsKey(key)) {
            modifiedTableHash.get(key).newValue = value;
        } else {
            modifiedTableHash.put(key, new OldAndNewValue(tableHash.get(key), value));
        }
    }

    private String getOldValue(String key) {
        if (modifiedTableHash.containsKey(key)) {
            return modifiedTableHash.get(key).newValue;
        }

        return tableHash.get(key);
    }

	public int commit() {
        int savedChangesNumber = 0;

        for (final String key: modifiedTableHash.keySet()) {
            OldAndNewValue oldAndNewValue = modifiedTableHash.get(key);
            if (!isEqual(oldAndNewValue.newValue, oldAndNewValue.oldValue)) {
                if (oldAndNewValue.newValue == null) {
                    tableHash.remove(key);
                } else {
                    tableHash.put(key, oldAndNewValue.newValue);
                }

                savedChangesNumber += 1;
            }
        }

        modifiedTableHash.clear();

        tableSize = tableHash.size();

        try {
            saveTable();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return 0;
        }

        unsavedChangesNumber = 0;

		return savedChangesNumber;
	}

	public int rollback() {
        int rollbackChangesNumber = 0;

        for (final String key: modifiedTableHash.keySet()) {
            OldAndNewValue oldAndNewValue = modifiedTableHash.get(key);
            if (!isEqual(oldAndNewValue.newValue, oldAndNewValue.oldValue)) {
                rollbackChangesNumber += 1;
            }
        }

		modifiedTableHash.clear();

        tableSize = tableHash.size();

		unsavedChangesNumber = 0;
		
		return rollbackChangesNumber;
	}

    private boolean isEqual(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }
}