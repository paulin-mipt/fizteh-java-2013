package ru.fizteh.fivt.students.surakshina.calculator;

import java.util.*;

public class Calculator {
    private final String INVALID_OPERATION = "Stack of operations has another symbols";
    private final int RADIX = 19;
    private Stack<Integer> numbers = new Stack<Integer>();
    private Stack<String> operations = new Stack<String>();

    private void input(String[] str) {
        if (str.length == 0) {
            System.err.println("Empty expression!");
            System.exit(1);
        }
    }

    private String getResult() {
        return Integer.toString(numbers.peek(), RADIX);
    }

    private int priority(String op) {
        if (op.equals("+") || op.equals("-")) {
            return 1;
        } else if (op.equals("*") || op.equals("/")) {
            return 2;
        } else if (op.equals("(")) {
            return 0;
        }
        throw new RuntimeException(INVALID_OPERATION);
    }

    private void checkOverflow(int num1, int num2, String op) {
        long n1 = num1;
        long n2 = num2;
        switch (op) {
        case "+":
            if (((n1 + n2) > Integer.MAX_VALUE)
                    || ((n1 + n2) < Integer.MIN_VALUE)) {
                throw new RuntimeException("Overflow!");
            }
            break;
        case "-":
            if (((n1 - n2) > Integer.MAX_VALUE)
                    || ((n1 - n2) < Integer.MIN_VALUE)) {
                throw new RuntimeException("Overflow!");
            }
            break;
        case "*":
            if (((n1 * n2) > Integer.MAX_VALUE)
                    || ((n1 * n2) < Integer.MIN_VALUE)) {
                throw new RuntimeException("Overflow!");
            }
            break;
        case "/":
            if (n2 == 0) {
                throw new ArithmeticException("Divide by zero");
            }
            if (((n1 / n2) > Integer.MAX_VALUE)
                    || ((n1 / n2) < Integer.MIN_VALUE)) {
                throw new RuntimeException("Overflow!");
            }
            break;
        default:
            throw new RuntimeException(INVALID_OPERATION);
        }
    }

    private void calculate(String op) {
        Integer second = numbers.pop();
        Integer first = numbers.pop();
        checkOverflow(first, second, op);
        Integer result = 0;
        switch (op) {
        case "+":
            result = second + first;
            break;
        case "-":
            result = first - second;
            break;
        case "*":
            result = first * second;
            break;
        case "/":
            if (second != 0) {
                result = first / second;
            } else {
                throw new ArithmeticException("Divide by zero");
            }
            break;
        default:
            throw new RuntimeException(INVALID_OPERATION);
        }
        numbers.push(result);
    }

    private String prepareToCalculate(String[] str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (int i = 0; i < str.length; ++i) {
            stringBuilder.append(str[i] + " ");
        }
        stringBuilder.append(")");
        String res = stringBuilder.toString();
        res = res.replace("+", " + ")
                .replace("-", " - ")
                .replace("*", " * ")
                .replace("/", " / ")
                .replace("(", " ( ")
                .replace(")", " ) ");
        return res;
    }

    private void doOperation(String op) {
        if (operations.isEmpty()) {
            operations.push(op);
        } else if (priority(op) > priority(operations.peek())) {
            operations.push(op);
        } else {
            while ((!operations.isEmpty())
                    && (priority(operations.peek()) >= priority(op))) {
                calculate(operations.pop());
            }
            operations.push(op);
        }
    }

    private void operationWithClosingBracket() {
        while (!operations.isEmpty() && (!operations.peek().equals("("))) {
            calculate(operations.pop());
        }
        if (!operations.isEmpty()) {
            operations.pop();
        }
    }

    private void analyzeInput(String inputString) {
        boolean nextIsNumber = true;
        int bracketBalance = 0;
        String operation = "\\+|\\-|\\*|\\/";
        Scanner scanner = new Scanner(inputString);
        scanner.useRadix(RADIX);
        while (scanner.hasNext()) {
            if (nextIsNumber
                    && (scanner.hasNextInt() || scanner.hasNext("\\("))) {
                if (scanner.hasNextInt()) {
                    numbers.push(scanner.nextInt());
                    nextIsNumber = false;
                } else if (scanner.hasNext("\\(")) {
                    operations.push(scanner.next());
                    ++bracketBalance;
                }
            } else if (!nextIsNumber
                    && (scanner.hasNext(operation) || scanner.hasNext("\\)"))) {
                if (scanner.hasNext(operation)) {
                    doOperation(scanner.next());
                    nextIsNumber = true;
                } else if (scanner.hasNext("\\)")) {
                    if (bracketBalance < 0) {
                        scanner.close();
                        throw new RuntimeException("Incorrect bracket balance");
                    }
                    operationWithClosingBracket();
                    scanner.next("\\)");
                    --bracketBalance;
                }
            } else {
                scanner.close();
                throw new RuntimeException("Another symbol!");
            }
        }
        scanner.close();
        if (bracketBalance != 0) {
            throw new RuntimeException("Incorrect bracket balance");
        }
    }

    public static void main(String[] args) {
        Calculator calc = new Calculator();
        String res = new String();
        calc.input(args);
        res = calc.prepareToCalculate(args);
        calc.analyzeInput(res);
        System.out.println(calc.getResult());
    }
}
