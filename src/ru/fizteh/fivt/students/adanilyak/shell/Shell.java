package ru.fizteh.fivt.students.adanilyak.shell;

import java.io.File;
import java.io.IOException;

public class Shell {
    // state - is a directory in which the shell is working
    private File state;
    private boolean typeOfRunning;

    public Shell() {
        state = new File("").getAbsoluteFile();
    }

    public File getState() {
        return state;
    }

    public void setInteractiveType() {
        typeOfRunning = false;
    }

    public void setPackageType() {
        typeOfRunning = true;
    }

    public boolean getTypeOfRunning() {
        return typeOfRunning;
    }

    public void changeState(String path) throws IOException {
        File newState = new File(this.getState(), path);
        if (!newState.exists()) {
            throw new IOException("'" + newState.getPath() + "': No such file or directory");
        }
        newState = newState.toPath().normalize().toFile();
        state = newState;
    }
}
