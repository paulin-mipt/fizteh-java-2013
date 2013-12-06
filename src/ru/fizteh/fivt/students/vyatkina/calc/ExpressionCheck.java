package ru.fizteh.fivt.students.vyatkina.calc;

import java.util.Scanner;

public class ExpressionCheck {

    static void operationOverflowCheck(int a, int b, char operation) throws IllegalArgumentException {
        long aLong = a;
        long bLong = b;
        boolean overflow = false;
        switch (operation) {
            case '+': {
                if ((aLong + bLong) > Integer.MAX_VALUE || (aLong + bLong) < Integer.MIN_VALUE) {
                    overflow = true;
                }
                break;
            }
            case '-': {
                if ((aLong - bLong) > Integer.MAX_VALUE || (aLong - bLong) < Integer.MIN_VALUE) {
                    overflow = true;
                }
                break;
            }
            case '*': {
                if ((aLong * bLong) > Integer.MAX_VALUE || (aLong * bLong) < Integer.MIN_VALUE) {
                    overflow = true;
                }
                break;
            }
            case '/': {
                nullDivisionCheck(a, b);
                if ((aLong / bLong) > Integer.MAX_VALUE || (aLong / bLong) < Integer.MIN_VALUE) {
                    overflow = true;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("operationOverflow: invalid operation");
            }
        }
        if (overflow) {
            throw new IllegalArgumentException("operation overflow");
        }
    }

    static void bracketBalanceCheck(String expression) throws IllegalArgumentException {
        int balance = 0;
        for (char a : expression.toCharArray()) {
            if (a == '(') {
                balance += 1;
            } else if (a == ')') {
                balance -= 1;
            }
            if (balance < 0) {
                throw new IllegalArgumentException("bracketBalanceCheck: bad balance");
            }

        }
        if (balance != 0) {
            throw new IllegalArgumentException("bracketBalanceCheck: bad balance");
        }
    }

    static void nullDivisionCheck(int a, int b) throws IllegalArgumentException {
        if (b == 0) {
            throw new IllegalArgumentException("nullDivisionCheck: null division");
        }
    }


    static void invalidExpressionCheck(String expression) throws IllegalArgumentException {
        //Let's think that we should have at list two operands and one operation
        // or only one operand and no operations
        //Also the number of operations should be one less then the number of operands
        //We couldn't have two operands or two operations in a row

        int numberOfOperations = 0;
        int numberOfOperands = 0;

        boolean nextIsOperand = true;
        String operation = "\\+|\\-|\\*|\\/";

        String whiteSpaceExpression = expression.replace("+", " + ").replace("-", " - ")
                .replace("*", " * ").replace("/", " / ").replace("(", " ( ").replace(")", " ) ");

        Scanner scanner = new Scanner(whiteSpaceExpression);
        scanner.useRadix(StackCalculator.RADIX);

        while (scanner.hasNext()) {
            if (nextIsOperand && scanner.hasNext("\\(")) {
                scanner.next("\\(");

            } else if (nextIsOperand && scanner.hasNextInt()) {
                scanner.nextInt();
                nextIsOperand = false;
                ++numberOfOperands;

            } else if (!nextIsOperand && scanner.hasNext("\\)")) {
                scanner.next("\\)");

            } else if (!nextIsOperand && scanner.hasNext(operation)) {
                scanner.next(operation);
                nextIsOperand = true;
                ++numberOfOperations;

            } else {
                throw new IllegalArgumentException("InvalidExpressionCheck: invalid expression " + scanner.next());
            }
        }

        if ((numberOfOperands < 2) || (numberOfOperations < 1)) {
            if (!(numberOfOperands == 1 && numberOfOperations == 0)) {
                throw new IllegalArgumentException("InvalidExpressionCheck: too few operations or operands");
            }
        }

    }


};
