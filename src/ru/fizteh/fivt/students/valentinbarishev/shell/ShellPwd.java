package ru.fizteh.fivt.students.valentinbarishev.shell;

final class ShellPwd extends SimpleShellCommand {
    private Context context;

    public ShellPwd(final Context newContext) {
        context = newContext;
        setName("pwd");
        setNumberOfArgs(1);
        setHint("usage: pwd");
    }

    @Override
    public void run() {
        System.out.println(context.getCurrentDir());
    }
}
