package ru.fizteh.fivt.students.visamsonov.storage;

import java.util.TreeMap;
import java.util.Map;
import java.math.BigInteger;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.*;

public class FileStorage implements TableInterface {

	private final File dbFilePath;
	private final Map<String, String> memoryStore;
	private final ThreadLocal<Map<String, String>> diffRemoved;
	private final ThreadLocal<Map<String, String>> diffAdded;
	private final Lock commitLock = new ReentrantLock(true);
	private final int MAX_KEY_LENGTH = 1024*1024;
	private final int MAX_VAL_LENGTH = 1024*1024;
	private final int MAX_TOTAL_LENGTH = 500*1024*1024;

	public FileStorage (String directory, String fileName) throws IOException {
		dbFilePath = new File(directory, fileName);
		memoryStore = new TreeMap<String, String>();
		diffRemoved = new ThreadLocal() {
			public Map<String, String> initialValue () {
				return new TreeMap<String, String>();
			}
		};
		diffAdded = new ThreadLocal() {
			public Map<String, String> initialValue () {
				return new TreeMap<String, String>();
			}
		};
		loadDataToMemory();
	}

	private void loadDataToMemory () throws IOException {
		if (!dbFilePath.isFile()) {
			return;
		}
		DataInputStream dbFile = new DataInputStream(new BufferedInputStream(new FileInputStream(dbFilePath)));
		IOException dataFormatError = new IOException("invalid data file");
		TreeMap<Integer, String> offsets = new TreeMap<Integer, String>();
		int currentOffset = 0;
		try {
			do {
				ByteArrayOutputStream keyBytes = new ByteArrayOutputStream();
				for (byte singleByte = dbFile.readByte(); singleByte != 0; singleByte = dbFile.readByte()) {
					keyBytes.write(singleByte);
					if (keyBytes.size() > MAX_KEY_LENGTH) {
						throw dataFormatError;
					}
				}
				currentOffset += keyBytes.size() + 1;
				int valueOffset = dbFile.readInt();
				currentOffset += 4;
				if (valueOffset < currentOffset) {
					throw dataFormatError;
				}
				offsets.put(valueOffset, new String(keyBytes.toByteArray(), StandardCharsets.UTF_8));
				if (currentOffset > offsets.firstKey() || currentOffset > MAX_TOTAL_LENGTH) {
					throw dataFormatError;
				}
			} while (currentOffset < offsets.firstKey());
			while (offsets.size() > 0) {
				Map.Entry<Integer, String> key = offsets.pollFirstEntry();
				if (key.getKey() != currentOffset || currentOffset > MAX_TOTAL_LENGTH) {
					throw dataFormatError;
				}
				if (offsets.size() == 0) {
					ByteArrayOutputStream valueBytes = new ByteArrayOutputStream();
					byte[] singleByte = new byte[1];
					for (int result = dbFile.read(singleByte); result == 1; result = dbFile.read(singleByte)) {
						valueBytes.write(singleByte);
						if (valueBytes.size() > MAX_VAL_LENGTH) {
							throw dataFormatError;
						}
					}
					currentOffset += valueBytes.size();
					if (currentOffset > MAX_TOTAL_LENGTH) {
						throw dataFormatError;
					}
					memoryStore.put(key.getValue(), new String(valueBytes.toByteArray(), StandardCharsets.UTF_8));
				}
				else {
					final int length = offsets.firstKey() - key.getKey();
					if (length > MAX_VAL_LENGTH) {
						throw dataFormatError;
					}
					currentOffset += length;
					byte[] valueBytes = new byte[length];
					dbFile.readFully(valueBytes);
					memoryStore.put(key.getValue(), new String(valueBytes, StandardCharsets.UTF_8));
				}
			}
		}
		catch (EOFException e) {
			if (currentOffset > 0) {
				throw dataFormatError;
			}
		}
	}

	private void actualizeRemoved () {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		for (String key : diffRemoved.keySet()) {
			if (memoryStore.get(key) == null) {
				diffRemoved.remove(key);
			}
		}
	}

	private void actualizeAdded () {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		for (Map.Entry<String, String> entry : diffAdded.entrySet()) {
			if (entry.getValue().equals(memoryStore.get(entry.getKey()))) {
				diffAdded.remove(entry.getKey());
			}
		}
	}

	private int calculateRepeated () {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		int result = 0;
		for (Map.Entry<String, String> entry : diffAdded.entrySet()) {
			if (entry.getValue().equals(memoryStore.get(entry.getKey()))) {
				result++;
			}
		}
		return result;
	}

	public String getName () {
		throw new UnsupportedOperationException();
	}

	public String get (String key) {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException();
		}
		commitLock.lock();
		try {
			actualizeRemoved();
			if (diffRemoved.get(key) != null) {
				return null;
			}
			if (diffAdded.get(key) != null) {
				return diffAdded.get(key);
			}
			String value = memoryStore.get(key);
			return value;
		}
		finally {
			commitLock.unlock();
		}
	}

	public String put (String key, String value) {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		if (key == null || value == null || key.trim().isEmpty() || value.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}
		commitLock.lock();
		try {
			actualizeRemoved();
			if (diffRemoved.get(key) != null) {
				if (!value.equals(diffRemoved.get(key))) {
					diffAdded.put(key, value);
				}
				diffRemoved.remove(key);
				return null;
			}
			if (diffAdded.get(key) != null) {
				return diffAdded.put(key, value);
			}
			String oldValue = memoryStore.get(key);
			diffAdded.put(key, value);
			return oldValue;
		}
		finally {
			commitLock.unlock();
		}
	}

	public String remove (String key) {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException();
		}
		String oldValue;
		commitLock.lock();
		try {
			actualizeRemoved();
			if (diffRemoved.get(key) != null) {
				commitLock.unlock();
				return null;
			}
			oldValue = memoryStore.get(key);
		}
		finally {
			commitLock.unlock();
		}
		if (oldValue != null) {
			diffRemoved.put(key, oldValue);
		}
		if (diffAdded.get(key) != null) {
			return diffAdded.remove(key);
		}
		return oldValue;
	}

	public int rollback () {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		int result;
		commitLock.lock();
		try {
			actualizeRemoved();
			result = diffAdded.size() + diffRemoved.size() - calculateRepeated();
		}
		finally {
			commitLock.unlock();
		}
		diffAdded.clear();
		diffRemoved.clear();
		return result;
	}

	public int size () {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		int result;
		commitLock.lock();
		try {
			actualizeRemoved();
			result = memoryStore.size() + diffAdded.size() - diffRemoved.size();
			for (String key : diffAdded.keySet()) {
				if (memoryStore.get(key) != null) {
					result--;
				}
			}
		}
		finally {
			commitLock.unlock();
		}
		return result;
	}

	public int unsavedChanges () {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		commitLock.lock();
		try {
			actualizeRemoved();
			return diffAdded.size() + diffRemoved.size() - calculateRepeated();
		}
		finally {
			commitLock.unlock();
		}
	}

	public int commit () {
		Map<String, String> diffAdded = this.diffAdded.get();
		Map<String, String> diffRemoved = this.diffRemoved.get();
		int result = 0;
		commitLock.lock();
		try {
			actualizeRemoved();
			actualizeAdded();
			result = diffAdded.size() + diffRemoved.size();
			for (String key : diffRemoved.keySet()) {
				memoryStore.remove(key);
			}
			for (Map.Entry<String, String> entry : diffAdded.entrySet()) {
				memoryStore.put(entry.getKey(), entry.getValue());
			}
			diffAdded.clear();
			diffRemoved.clear();
			if (memoryStore.size() == 0) {
				dbFilePath.delete();
				return result;
			}
			dbFilePath.createNewFile();
			DataOutputStream dbFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dbFilePath)));
			int dataOffset = 0;
			for (String key : memoryStore.keySet()) {
				dataOffset += key.getBytes(StandardCharsets.UTF_8).length + 5;
			}
			for (Map.Entry<String, String> entry : memoryStore.entrySet()) {
				dbFile.write(entry.getKey().getBytes(StandardCharsets.UTF_8));
				dbFile.write(0);
				dbFile.writeInt(dataOffset);
				dataOffset += entry.getValue().getBytes(StandardCharsets.UTF_8).length;
			}
			for (Map.Entry<String, String> entry : memoryStore.entrySet()) {
				dbFile.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
			}
			dbFile.close();
		}
		catch (IOException e) {}
		finally {
			commitLock.unlock();
		}
		return result;
	}
}