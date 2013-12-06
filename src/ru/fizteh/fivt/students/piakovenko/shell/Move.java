package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class Move implements Commands {
    private final String name = "mv";
    private CurrentStatus currentStatus;

    public Move(CurrentStatus cs) {
        currentStatus = cs;
    }

    public String getName() {
        return name;
    }


    public void perform(String[] array) throws IOException {
        if (array.length != 3) {
            throw new IOException("Wrong arguments! Usage mv <source> <destination>");
        }
        Copy c = new Copy(currentStatus);
        Remove r = new Remove(currentStatus);
        String[] temp = new String[2];
        for (int i = 0; i < 2; ++i) {
            temp[i] = array[i];
        }
        c.perform(array);
        r.perform(temp);
    }
}
