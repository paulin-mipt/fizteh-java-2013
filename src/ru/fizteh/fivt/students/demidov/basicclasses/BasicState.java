package ru.fizteh.fivt.students.demidov.basicclasses;

import java.io.IOException;

public interface BasicState {
	String get(String key) throws IOException;
	String put(String key, String value) throws IOException;
	String remove(String key) throws IOException;
}
