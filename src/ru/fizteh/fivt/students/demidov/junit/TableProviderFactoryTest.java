package ru.fizteh.fivt.students.demidov.junit;

import org.junit.Test;

public class TableProviderFactoryTest {
	@Test(expected = IllegalArgumentException.class)
	public void createTableProviderWithNullParameter() {
		TableProviderFactoryImplementation factory = new TableProviderFactoryImplementation();
		factory.create(null);
	}
}
