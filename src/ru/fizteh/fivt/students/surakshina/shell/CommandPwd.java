package ru.fizteh.fivt.students.surakshina.shell;

public class CommandPwd extends AbstractCommand {
    public CommandPwd(State stateNew) {
        super(stateNew);
        name = "pwd";
        numberOfArguments = 0;
    }

    @Override
    public void executeProcess(String[] input) {
        System.out.println(state.getCurrentDirectory());
    }
}
