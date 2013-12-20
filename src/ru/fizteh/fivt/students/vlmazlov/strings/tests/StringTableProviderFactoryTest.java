package ru.fizteh.fivt.students.vlmazlov.strings.tests;

import org.junit.*;

import ru.fizteh.fivt.students.vlmazlov.strings.StringTableProviderFactory;

public class StringTableProviderFactoryTest {
	private StringTableProviderFactory factory;

	@Before
	public void setUp() {
		factory = new StringTableProviderFactory();
	}

	@Test(expected = IllegalArgumentException.class)
	public void creatingNullShouldFail() {
		factory.create(null);
	}
} 