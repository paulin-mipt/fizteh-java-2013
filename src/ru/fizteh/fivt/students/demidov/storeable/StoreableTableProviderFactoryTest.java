package ru.fizteh.fivt.students.demidov.storeable;

import java.io.IOException;

import org.junit.Test;

public class StoreableTableProviderFactoryTest {
	@Test(expected = IllegalArgumentException.class)
	public void createTableProviderWithNullParameter() throws IOException {
		StoreableTableProviderFactory factory = new StoreableTableProviderFactory();
		factory.create(null);
	}
}
