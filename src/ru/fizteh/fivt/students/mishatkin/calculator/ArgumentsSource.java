/**
 * ArgumentsSource.java
 * calculator
 *
 * Created by Vladimir Mishatkin on 9/17/13
 */

package ru.fizteh.fivt.students.mishatkin.calculator;

public class ArgumentsSource implements InputSource {

    private int nextArgumentIndex = 0;
    private String[] args;

    public ArgumentsSource(String[] args) {
        this.args = args;
    }

    @Override
    public boolean hasNextLine() {
        return (nextArgumentIndex < args.length);
    }

    @Override
    public String nextLine() {
        return args[nextArgumentIndex++];
    }
}
