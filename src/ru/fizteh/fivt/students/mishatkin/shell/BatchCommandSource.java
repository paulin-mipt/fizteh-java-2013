package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * BatchCommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 *
 */
public class BatchCommandSource extends CommandSource {
	private int nextArgumentIndex = 0;
	private String[] args;

	public BatchCommandSource(String[] args) {
		this.args = args;
	}

	@Override
	public boolean hasMoreData() {
		return nextArgumentIndex < args.length;
	}

	@Override
	public String nextLine() {
		StringBuilder mergedArgumentBuilder = new StringBuilder();
		while (hasMoreData()) {
			mergedArgumentBuilder.append(args[nextArgumentIndex++]).append(" ");
		}
		return new String(mergedArgumentBuilder);
	}

}
