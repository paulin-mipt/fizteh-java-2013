package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * StandardInputCommandSource.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

import java.util.Scanner;

public class StandardInputCommandSource extends CommandSource {
	private Scanner in;


	public StandardInputCommandSource(Scanner in) {
		this.in = in;
	}

	@Override
	public boolean hasMoreData() {
		return in.hasNext();
	}

	@Override
	public String nextLine() {
		return in.nextLine();
	}

}
