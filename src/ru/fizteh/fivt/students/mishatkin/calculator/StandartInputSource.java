/**
 * StandardInputSource.java
 * calculator
 *
 * Created by Vladimir Mishatkin on 9/17/13
 */

package ru.fizteh.fivt.students.mishatkin.calculator;

import java.util.Scanner;

public class StandartInputSource implements InputSource {
    private Scanner in;

    StandartInputSource(Scanner in) {
        this.in = in;
    }

    @Override
    public boolean hasNextLine() {
        return in.hasNextLine();
    }

    @Override
    public String nextLine() {
        return in.nextLine();
    }
}
