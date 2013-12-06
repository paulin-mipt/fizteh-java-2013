package ru.fizteh.fivt.students.adanilyak.shell;

public class CmdPwd {
    private final RequestCommandType name = RequestCommandType.getType("pwd");
    private final int amArgs = 0;

    public RequestCommandType getReqComType() {
        return name;
    }

    public int getAmArgs() {
        return amArgs;
    }

    public void work(Shell shell) {
        System.out.println(shell.getState().getPath());
    }
}
