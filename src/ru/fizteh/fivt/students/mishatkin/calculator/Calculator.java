/**
 * calculator.java
 * calculator
 *
 * Created by Vladimir Mishatkin on 9/14/13
 */

package ru.fizteh.fivt.students.mishatkin.calculator;

import java.io.*;
import java.lang.*;
import java.util.*;

import static java.lang.String.valueOf;

public class Calculator {
    public static int MyBase = 19;
    private static boolean shouldUseArgsInsteadOfSTDIN = true;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Should run with positive number of arguments.");
            System.exit(1);
        }
        InputStream inputStream = System.in;
        OutputStream outputStream = System.out;
        PrintWriter out = new PrintWriter(outputStream);
        InputSource in = shouldUseArgsInsteadOfSTDIN ? new ArgumentsSource(args) :
                         new StandartInputSource(new Scanner(inputStream));
        Task solver = new Task(in, out);
        int retValue = solver.solve();
        out.close();
        System.exit(retValue);
    }
}