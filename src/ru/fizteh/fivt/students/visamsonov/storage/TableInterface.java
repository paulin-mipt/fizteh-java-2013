package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.Table;

public interface TableInterface extends Table {
	/* Этот костыль ради этой функции... FUUUUU!!! */
	int unsavedChanges ();
}