/**
 * Task.java
 * Caclulator
 *
 * Created by Vladimir Mishatkin on 9/20/13
 */

package ru.fizteh.fivt.students.mishatkin.calculator;

import java.io.PrintWriter;

class Task {
    private InputSource in;
    private PrintWriter out;

    Task(InputSource in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    private void printInNumericSystem(int decimalResult, int base) {
        out.println(Integer.toString(decimalResult, base));
    }

    public int solve() {
        ReversePolishNoteEncoder encoder = new ReversePolishNoteEncoder();
        boolean isValidInput = true;
        int calculationResultDecimal = 0;
        while (isValidInput && in.hasNextLine()) {
            try {
                calculationResultDecimal = encoder.calculate(in.nextLine());
            } catch (Exception e) {
                System.err.println(e.getMessage());
                isValidInput = false;
            }
        }
        if (isValidInput) {
            printInNumericSystem(calculationResultDecimal, Calculator.MyBase);
        } else {
            return 1;
        }
        return 0;
    }

}