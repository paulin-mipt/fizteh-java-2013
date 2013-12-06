package ru.fizteh.fivt.students.msandrikova.shell;

public abstract class Command {
    
    private String name;
    private int argNum;
    
    protected Command(String name, int argNum) {
        this.name = name;
        this.argNum = argNum;
    }
    
    public String getName() {
        return name;
    }
    
    public int getArgNum() {
        return argNum;
    }
    
    protected boolean getArgsAcceptor(int argNum, boolean isInteractive) {
        if (this.argNum != argNum) {
            if (this.argNum == 1) {
                Utils.generateAnError("You should enter " + this.argNum 
                        + " argument", this.getName(), isInteractive);
            } else {
                Utils.generateAnError("You should enter " + this.argNum 
                        + " arguments", this.getName(), isInteractive);
            }
            return false;
        }
        return true;
    }
    
    public abstract void execute(String[] argumentsList, Shell shell);
}
