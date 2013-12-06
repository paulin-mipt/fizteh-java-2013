package ru.fizteh.fivt.students.paulinMatavina;

import java.util.Scanner;
import java.util.Stack;
import java.util.zip.DataFormatException;
import java.util.regex.Pattern;

public class Calculator {
    //get priority for an operator
    private static int getPriority(final String oper) {
        switch (oper) {
            case "(":
                return -1;
            case "+":
                return 2;
            case "-":
                return 2;
            case "*":
                return 1;
            case "/":
                return 1;
            default:
                return 0;
        }
    }

    private static boolean correctSymbolsOnly(final String str) {
        return Pattern.matches("[0-9a-gA-G\\s/(/)/+-/*//]*", str);
    }

    private static boolean spaceBetweenNumbers(final String str) {
        return Pattern.matches("(.*[0-9a-gA-G]+([\\s]+)[0-9a-gA-G])+.*", str);
    }

    private static int tryPopInt(Stack<Integer> numStack)
            throws DataFormatException {
        if (numStack.empty()) {
            System.err.println("Error: incorrect expression");
            throw new DataFormatException();
        }
        return numStack.pop();
    }

    private static void makeOperation(Stack<String> operStack,
            Stack<Integer> numStack) throws DataFormatException {
        if (operStack.empty()) {
            System.err.println("Error: incorrect expression");
            throw new DataFormatException();
        }

        String operator = operStack.pop();
        if (operator.equals("(")) {
            operStack.push(operator);
            return;
        }

        int a;
        int b;
        try {
            a = tryPopInt(numStack);
            b = tryPopInt(numStack);
        } catch (DataFormatException e) {
            throw new DataFormatException();
        }
        switch (operator) {
            case "+":
                if (((a > 0) && (Integer.MAX_VALUE - a < b))
                        || ((a < 0) && (Integer.MIN_VALUE - a > b))) {
                    throw new ArithmeticException("integer overflow");
                }
                numStack.push(a + b);
                break;
            case "-":
                if (((a > 0) && (Integer.MAX_VALUE - a < -b))
                        || ((a < 0) && (Integer.MIN_VALUE - a > -b))) {
                    throw new ArithmeticException("integer overflow");
                }
                numStack.push(b - a);
                break;
            case "*":
                int signA = a;
                if (a != 0) {
                    signA = a / Math.abs(a);
                }
                int signB = b;
                if (b != 0) {
                    signB = b / Math.abs(b);
                }
                if (signA * signB == 1 && ((Integer.MAX_VALUE / a) < b)
                        || ((signA * signB == -1)
                                && ((Integer.MIN_VALUE / a) > b))) {
                    throw new ArithmeticException("integer overflow");
                }
                numStack.push(a * b);
                break;
            case "/":
                if (a == 0) {
                    throw new ArithmeticException("division by zero");
                }
                numStack.push(b / a);
                break;
            default:
                break;
        }
    }

    //main
    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Put arithmetical expression as an argument.");
            System.out.println("Use [0..9, a..g, A..G] [+, -, *, /, (, )]");
            System.exit(1);
        }

        final int base = 17;

        StringBuilder exp = new StringBuilder();
        exp.append('(');
        for (int i = 0; i < args.length; i++) {
            exp.append(args[i]);
            exp.append(" ");
        }
        exp.append(')');
        String expString = exp.toString();

        if (!correctSymbolsOnly(expString)) {
            System.err.println("Wrong symbols in expression");
            System.exit(1);
        }

        if (spaceBetweenNumbers(expString)) {
            System.err.println("Spaces in wrong places");
            System.exit(1);
        }

        expString = expString.replace(" ", "");

        Stack<Integer> numStack = new Stack<Integer>();
        Stack<String> operStack = new Stack<String>();

        Scanner scanner = new Scanner(expString);

        scanner.useRadix(base);
        scanner.useDelimiter("((?<=[0-9a-gA-G])(?=[/(/)/+-/*//]))"
                + "|((?<=[/(/)/+-/*//])(?=[0-9a-gA-G]))"
                + "|((?<=[/(/)/+-/*//])(?=[/(/)/+-/*//]))");

        String operator;
        while (scanner.hasNext()) {
            if (scanner.hasNextInt()) {
                numStack.push(scanner.nextInt());
            } else {
                operator = scanner.next();
                switch (operator) {
                case "(":
                    operStack.push(operator);
                    break;
                case ")":
                    int prior = getPriority(operStack.peek());
                    while (prior > 0) {
                        prior = getPriority(operStack.peek());
                        try {
                            makeOperation(operStack, numStack);
                        } catch (DataFormatException e) {
                            scanner.close();
                            System.exit(1);
                        } catch (ArithmeticException e) {
                            scanner.close();
                            System.out.println("Arithmetic exception: "
                                    + e.getMessage());
                            System.exit(1);
                        }
                    }
                    operStack.pop();
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    prior = getPriority(operStack.peek());
                    while (prior > 0 && prior <= getPriority(operator)) {
                        try {
                            makeOperation(operStack, numStack);
                        } catch (DataFormatException e) {
                            scanner.close();
                            System.exit(1);
                        } catch (ArithmeticException e) {
                            scanner.close();
                            System.out.println("Arithmetic exception: "
                                    + e.getMessage());
                            System.exit(1);
                        }
                        prior = getPriority(operStack.peek());
                    }
                    operStack.push(operator);
                    break;
                default:
                    break;
                }
            }
        }
        scanner.close();

        if (!operStack.empty() || numStack.size() != 1) {
            System.err.println("Error: incorrect expression");
            System.exit(1);
        }

        int answer = numStack.pop();
        System.out.println(Integer.toString(answer, base).toUpperCase());
    }
}
