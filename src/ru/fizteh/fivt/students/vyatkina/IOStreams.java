package ru.fizteh.fivt.students.vyatkina;

import java.io.InputStream;
import java.io.PrintStream;

public class IOStreams {

    public final InputStream in;
    public final PrintStream out;
    public final PrintStream err;

    public IOStreams() {
        in = System.in;
        out = System.out;
        err = System.err;
    }

    public IOStreams(InputStream in, PrintStream out, PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }
}
