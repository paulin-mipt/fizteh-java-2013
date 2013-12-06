package ru.fizteh.fivt.students.valentinbarishev.shell;

final class ShellDir extends SimpleShellCommand {
    private Context context;

    public ShellDir(final Context newContext) {
        context = newContext;
        setName("dir");
        setNumberOfArgs(1);
        setHint("usage: dir");
    }

    @Override
    public void run() {
        String[] content = context.getDirContent();
        for (int i = 0; i < content.length; ++i) {
            System.out.println(content[i]);
        }
    }

}
