package ru.fizteh.fivt.students.adanilyak.calculator;

/**
 * User: Alexander
 * Date: 29.09.13
 * Time: 15:00
 */

public class SafeOperations {

    static final int safeAdd(int left, int right) {
        if (right > 0 ? left > Integer.MAX_VALUE - right
                : left < Integer.MIN_VALUE - right) {
            System.err.print("integer overflow");
            System.exit(2);
        }
        return left + right;
    }

    static final int safeSubtract(int left, int right) {
        if (right > 0 ? left < Integer.MIN_VALUE + right
                : left > Integer.MAX_VALUE + right) {
            System.err.print("integer overflow");
            System.exit(2);
        }
        return left - right;
    }

    static final int safeMultiply(int left, int right) {
        if (right > 0 ? left > Integer.MAX_VALUE / right
                || left < Integer.MIN_VALUE / right
                : (right < -1 ? left > Integer.MIN_VALUE / right
                || left < Integer.MAX_VALUE / right
                : right == -1
                && left == Integer.MIN_VALUE)) {
            System.err.print("integer overflow");
            System.exit(2);
        }
        return left * right;
    }
}
