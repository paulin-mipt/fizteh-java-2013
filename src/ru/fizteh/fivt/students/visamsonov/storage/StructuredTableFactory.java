package ru.fizteh.fivt.students.visamsonov.storage;

import java.io.*;

public class StructuredTableFactory implements StructuredTableProviderFactoryInterface {

	public StructuredTableProviderInterface create (String dir) throws IOException {
		if (dir == null || dir.trim().isEmpty() || new File(dir).isFile()) {
			throw new IllegalArgumentException();
		}
		new File(dir).mkdir();
		if (!(new File(dir).isDirectory())) {
			 throw new IOException();
		}
		return new StructuredTableDirectory(dir);
	}
}