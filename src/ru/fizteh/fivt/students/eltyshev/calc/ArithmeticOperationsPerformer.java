package ru.fizteh.fivt.students.eltyshev.calc;

public class ArithmeticOperationsPerformer {
    public static int add(int arg1, int arg2) {
        if (arg1 > 0 && arg2 > 0) {
            if (arg1 > (Integer.MAX_VALUE - arg2)) {
                throw new ArithmeticException("Integer overflow error");
            }
        } else if (arg1 < 0 && arg2 < 0) {
            if (arg1 < Integer.MIN_VALUE - arg2) {
                throw new ArithmeticException("Integer overflow error");
            }
        }
        return arg1 + arg2;
    }

    public static int sub(int arg1, int arg2) {
        if ((arg1 > 0 && arg2 < 0) || (arg1 < 0 && arg2 > 0)) {
            if (Math.abs(arg1) > Math.abs(Integer.MAX_VALUE - arg2)) {
                throw new ArithmeticException("Integer overflow error");
            }
        }
        return arg1 - arg2;
    }

    public static int mul(int arg1, int arg2) {
        if (arg1 == 0) {
            return 0;
        }
        if (Math.abs(arg2) > (Integer.MAX_VALUE / Math.abs(arg1))) {
            throw new ArithmeticException("Integer overflow error");
        }
        return arg1 * arg2;
    }

    public static int div(int arg1, int arg2) {
        if (arg2 == 0) {
            throw new ArithmeticException("Division by zero!");
        }
        return arg1 / arg2;
    }

    public static int pow(int arg1, int arg2) {
        double log = Math.log(Integer.MAX_VALUE) / Math.log(arg1);
        if (arg2 > log) {
            throw new ArithmeticException("Integer overflow error");
        }
        return (int) Math.pow(arg1, arg2);
    }
}
