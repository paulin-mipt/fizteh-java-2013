package ru.fizteh.fivt.students.dubovpavel.calculator;

class SafeInteger {
    private static String getOverflowExceptionText() {
        return "Integer overflow.";
    }

    public static Integer add(Integer l, Integer r) throws ArithmeticException {
        if (r > 0 && l > Integer.MAX_VALUE - r
                || r < 0 && l < Integer.MIN_VALUE - r) {
            throw new ArithmeticException(getOverflowExceptionText());
        } else {
            return l + r;
        }
    }

    public static Integer subtract(Integer l, Integer r) throws ArithmeticException {
        if (r > 0 && l < Integer.MIN_VALUE + r
                || r < 0 && l > Integer.MAX_VALUE + r) {
            throw new ArithmeticException(getOverflowExceptionText());
        } else {
            return l - r;
        }
    }

    public static Integer multiply(Integer l, Integer r) throws ArithmeticException {
        if (r > 0
                && (l > Integer.MAX_VALUE / r || l < Integer.MIN_VALUE / r)
            || r == -1 && l == Integer.MIN_VALUE
            || r < -1
                && (l > Integer.MIN_VALUE / r || l < Integer.MAX_VALUE / r)) {
            throw new ArithmeticException(getOverflowExceptionText());
        } else {
            return l * r;
        }
    }

    public static Integer divide(Integer l, Integer r) throws ArithmeticException {
        if (l == Integer.MIN_VALUE && r == -1) {
            throw new ArithmeticException(getOverflowExceptionText());
        } else {
            return l / r;
        }
    }
}
