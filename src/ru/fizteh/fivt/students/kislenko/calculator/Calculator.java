package ru.fizteh.fivt.students.kislenko.calculator;

import java.io.IOException;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

import static java.lang.Integer.MAX_VALUE;

public class Calculator {
    private static final int RADIX = 17;

    private static String buildInputString(String[] parameters) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String parameter : parameters) {
            sb.append(parameter).append(" ");
        }
        String inputString = sb.toString();
        if (inputString.length() == 0) {
            System.err.println("Empty input.");
            System.exit(1);
        }
        return inputString;
    }

    private static String toPolandNotation(String input) throws IOException {
        Scanner scan = new Scanner(input);
        Stack<String> stack = new Stack<String>();
        StringBuilder polandBuilder = new StringBuilder();
        String s;
        scan.useRadix(RADIX);
        boolean minus = false;
        boolean changeSign = false;

        while (scan.hasNext()) {
            if (scan.hasNext("[0-9A-G][0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[\\s\\(\\)\\+\\-\\*\\/]");
                int value;
                try {
                    value = scan.nextInt();
                } catch (InputMismatchException e) {
                    throw new NumberFormatException("Too big value in input.");
                }
                if (changeSign) {
                    value = -value;
                    changeSign = false;
                }
                polandBuilder.append(value).append(" ");
                minus = true;
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\([0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                minus = false;
                scan.useDelimiter("[0-9A-G\\s\\)\\+\\-\\*\\/]");
                String brackets = scan.next("\\(*");
                for (int i = 0; i < brackets.length(); ++i) {
                    stack.push("(");
                }
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\)[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                minus = true;
                scan.useDelimiter("[0-9A-G\\s\\(\\+\\-\\*\\/]");
                String brackets = scan.next("\\)*");
                for (int i = 0; i < brackets.length(); ++i) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        polandBuilder.append(stack.pop()).append(" ");
                    }
                    if (stack.isEmpty()) {
                        System.err.println("Expression not complied with the bracket balance.");
                        System.exit(2);
                    }
                    stack.pop();
                }
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\+[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\-\\*\\/]");
                String buf = scan.next("\\+*");
                if (buf.length() > 1) {
                    System.err.println("Two or more pluses in succession.");
                    System.exit(3);
                }
                minus = false;
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    polandBuilder.append(stack.pop()).append(" ");
                }
                stack.push("+");
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\-[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\*\\/]");
                String minuses = scan.next("\\-*");
                if (minuses.length() > 1 || !minus) {
                    if (minus) {
                        if (minuses.length() % 2 == 0) {
                            changeSign = !changeSign;
                        }
                        stack.push("-");
                    } else {
                        if (minuses.length() % 2 == 1) {
                            changeSign = !changeSign;
                        }
                    }
                    minus = false;
                }
                if (minus) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        polandBuilder.append(stack.pop()).append(" ");
                    }
                    stack.push("-");
                }
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\*[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\-\\/]");
                String buf = scan.next("\\**");
                if (buf.length() > 1) {
                    System.err.println("Two or more multiples in succession.");
                    System.exit(3);
                }
                minus = false;
                while (!stack.isEmpty() && (stack.peek().equals("*") || stack.peek().equals("/"))) {
                    polandBuilder.append(stack.pop()).append(" ");
                }
                stack.push("*");
                scan.useDelimiter("\\s");
            } else if (scan.hasNext("\\/[0-9A-G\\s\\(\\)\\+\\-\\*\\/]*")) {
                scan.useDelimiter("[0-9A-G\\s\\(\\)\\+\\-\\*]");
                String buf = scan.next("\\/*");
                if (buf.length() > 1) {
                    System.err.println("Two or more divides in succession.");
                    System.exit(3);
                }
                minus = false;
                while (!stack.isEmpty() && (stack.peek().equals("*") || stack.peek().equals("/"))) {
                    polandBuilder.append(stack.pop()).append(" ");
                }
                stack.push("/");
                scan.useDelimiter("\\s");
            } else {
                System.err.println("Some bad symbol in input text.");
                System.exit(4);
            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) {
                System.err.println("Expression not complied with the bracket balance.");
                System.exit(5);
            }
            polandBuilder.append(stack.pop()).append(" ");
        }
        s = polandBuilder.toString();
        if (s.equals("")) {
            System.err.println("Empty input.");
            System.exit(1);
        }

        return s;
    }

    private static Integer calculate(String s) throws IOException {
        String[] symbols = s.split(" ");
        Stack<Integer> operandStack = new Stack<Integer>();
        int oper1;
        int oper2;

        for (String symbol : symbols) {
            if (symbol.equals("+")) {
                if (operandStack.size() < 2) {
                    System.err.println("Too many operations in this expression.");
                    System.exit(6);
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                if (Integer.signum(oper1) == Integer.signum(oper2)) {
                    if (MAX_VALUE - StrictMath.abs(oper1) < StrictMath.abs(oper2)) {
                        System.err.println("Too big values in expression.");
                        System.exit(7);
                    }
                }
                operandStack.push(oper1 + oper2);
            } else if (symbol.equals("-")) {
                if (operandStack.size() < 2) {
                    System.err.println("Too many operations in this expression.");
                    System.exit(6);
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                if (Integer.signum(oper1) != Integer.signum(oper2)) {
                    if (MAX_VALUE - StrictMath.abs(oper1) < StrictMath.abs(oper2)) {
                        System.err.println("Too big values in expression.");
                        System.exit(7);
                    }
                }
                operandStack.push(oper1 - oper2);
            } else if (symbol.equals("*")) {
                if (operandStack.size() < 2) {
                    System.err.println("Too many operations in this expression.");
                    System.exit(6);
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                if (oper2 != 0 && MAX_VALUE / StrictMath.abs(oper2) < StrictMath.abs(oper1)) {
                    System.err.println("Too big values in expression.");
                    System.exit(7);
                }
                operandStack.push(oper1 * oper2);
            } else if (symbol.equals("/")) {
                if (operandStack.size() < 2) {
                    System.err.println("Too many operations in this expression.");
                    System.exit(6);
                }
                oper2 = operandStack.pop();
                oper1 = operandStack.pop();
                try {
                    operandStack.push(oper1 / oper2);
                } catch (ArithmeticException e) {
                    throw new ArithmeticException("Dividing by zero.");
                }
            } else {
                operandStack.push(Integer.parseInt(symbol));
            }
        }

        Integer answer = operandStack.pop();
        if (!operandStack.isEmpty()) {
            System.err.println("Too many operands.");
            System.exit(8);
        }

        return answer;
    }

    public static void outputResult(Integer result) {
        PrintStream ps = new PrintStream(System.out);
        ps.print(Integer.toString(result, RADIX));
    }

    public static void main(String[] args) throws IOException, InputMismatchException {
        try {
            String inputString = buildInputString(args);
            String s = toPolandNotation(inputString);
            Integer result = calculate(s);
            outputResult(result);
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            System.exit(9);
        } catch (ArithmeticException e) {
            System.err.println(e.getMessage());
            System.exit(10);
        }
    }
}
