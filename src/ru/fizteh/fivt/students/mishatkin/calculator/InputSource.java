/**
 * InputSource.java
 * calculator
 *
 * Created by Vladimir Mishatkin on 9/17/13
 */

package ru.fizteh.fivt.students.mishatkin.calculator;

public interface InputSource {
    public boolean hasNextLine();
    public String nextLine();
}
