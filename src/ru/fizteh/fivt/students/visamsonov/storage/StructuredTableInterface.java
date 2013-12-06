package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.structured.Table;

public interface StructuredTableInterface extends Table {
	/* Этот костыль ради этой функции... FUUUUU!!! */
	int unsavedChanges ();
}