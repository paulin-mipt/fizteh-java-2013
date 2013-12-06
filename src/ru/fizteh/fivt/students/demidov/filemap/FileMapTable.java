package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.basicclasses.BasicTable;

public class FileMapTable extends BasicTable<String> {
	public FileMapTable(String path, String tableName) throws IOException {
		super(path, tableName);
	}

	public String serialize(String value) throws IOException {
		return value;
	}

	public String deserialize(String value) throws IOException {
		return value;
	}
}
