package ru.fizteh.fivt.students.anastasyev.calculator;

import java.util.Stack;

public class Main {
    private static final int RADIX = 17;

    private static Boolean numberChecker(String[] args, int i, int j) {
        return ((args[i].charAt(j) >= '0' && args[i].charAt(j) <= '9')
                || (args[i].charAt(j) >= 'A' && args[i].charAt(j) <= 'G')
                || (args[i].charAt(j) >= 'a' && args[i].charAt(j) <= 'g'));
    }

    private static String reversePolishNotationConversation(String[] args) {
        StringBuilder wholeString = new StringBuilder();
        Stack<String> symbolStack = new Stack();
        boolean minus = true;
        boolean sign = false;
        boolean emptyBrackets = false;
        for (int i = 0; i < args.length; ++i) {
            for (int j = 0; j < args[i].length(); ++j) {
                if (numberChecker(args, i, j)) {
                    int k = j + 1;
                    while (k < args[i].length() && numberChecker(args, i, k)) {
                        ++k;
                    }
                    int number = Integer.parseInt(args[i].substring(j, k), RADIX);
                    if (sign) {
                        number = -number;
                        sign = false;
                    }
                    wholeString.append(number).append(" ");
                    minus = false;
                    j += k - j - 1;
                    if (emptyBrackets) {
                        emptyBrackets = false;
                    }
                } else if (args[i].charAt(j) == '(') {
                    emptyBrackets = true;
                    int k = j + 1;
                    int count = 1;
                    while (k < args[i].length() && (args[i].charAt(k) == '(' || args[i].charAt(k) == ' ')) {
                        if (args[i].charAt(j) == '(') {
                            ++count;
                        }
                        ++k;
                    }
                    for (int p = 0; p < count; ++p) {
                        symbolStack.push("(");
                    }
                    minus = true;
                    j += count - 1;
                } else if (args[i].charAt(j) == ')') {
                    if (emptyBrackets) {
                        System.err.println("Empty brackets");
                        System.exit(1);
                    }
                    while (j < args[i].length() && (args[i].charAt(j) == ')' || args[i].charAt(j) == ' ')) {
                        if (args[i].charAt(j) == ' ') {
                            ++j;
                        } else {
                            while (!symbolStack.empty() && !symbolStack.peek().equals("(")) {
                                wholeString.append(symbolStack.pop()).append(" ");
                            }
                            if (symbolStack.empty()) {
                                System.err.println("Incorrect bracket balance!");
                                System.exit(1);
                            }
                            symbolStack.pop();
                            ++j;
                        }
                    }
                    minus = false;
                    --j;
                } else if (args[i].charAt(j) == '+') {
                    while (!symbolStack.empty() && !symbolStack.peek().equals("(")) {
                        wholeString.append(symbolStack.pop()).append(" ");
                    }
                    minus = false;
                    symbolStack.push("+");
                    if (emptyBrackets) {
                        emptyBrackets = false;
                    }
                } else if (args[i].charAt(j) == '-') {
                    if (minus) {
                        sign = true;
                    } else {
                        while (!symbolStack.empty() && !symbolStack.peek().equals("(")) {
                            wholeString.append(symbolStack.pop()).append(" ");
                        }
                        symbolStack.push("-");
                    }
                    if (emptyBrackets) {
                        emptyBrackets = false;
                    }
                } else if (args[i].charAt(j) == '*') {
                    while (!symbolStack.empty() && (symbolStack.peek().equals("*") || symbolStack.peek().equals("/"))) {
                        wholeString.append(symbolStack.pop()).append(" ");
                    }
                    minus = false;
                    symbolStack.push("*");
                    if (emptyBrackets) {
                        emptyBrackets = false;
                    }
                } else if (args[i].charAt(j) == '/') {
                    while (!symbolStack.empty() && (symbolStack.peek().equals("*") || symbolStack.peek().equals("/"))) {
                        wholeString.append(symbolStack.pop()).append(" ");
                    }
                    minus = false;
                    symbolStack.push("/");
                    if (emptyBrackets) {
                        emptyBrackets = false;
                    }
                } else if (Character.isWhitespace(args[i].charAt(j))) {
                    //
                } else {
                    System.err.println("Bad symbol: " + args[i].charAt(j));
                    System.exit(1);
                }
            }
        }
        while (!symbolStack.isEmpty()) {
            if (symbolStack.peek().equals("(")) {
                System.err.println("Incorrect bracket balance!");
                System.exit(1);
            }
            wholeString.append(symbolStack.pop()).append(" ");
        }
        return wholeString.toString();
    }

    private static void operationsChecker(Stack<Integer> values) {
        if (values.size() < 2) {
            System.err.println("Too many operations");
            System.exit(1);
        }
    }

    private static Integer calcs(String expression) {
        if (expression.length() == 0) {
            System.err.println("Invalid expression");
            System.exit(1);
        }
        Stack<Integer> values = new Stack<Integer>();
        String[] symbols = expression.split(" ");
        int value1;
        int value2;

        for (String symbol : symbols) {
            if (symbol.equals("+")) {
                operationsChecker(values);
                value2 = values.pop();
                value1 = values.pop();
                if ((value1 > 0 && value2 > 0 && Integer.MAX_VALUE - value1 <= value2)
                        || (value1 < 0 && value2 < 0 && Integer.MIN_VALUE - value2 >= value1)) {
                    System.err.println("Integer overflow: " + value1 + "+" + value2);
                    System.exit(1);
                }
                values.push(value1 + value2);
            } else if (symbol.equals("-")) {
                operationsChecker(values);
                value2 = values.pop();
                value1 = values.pop();
                if ((value2 > 0 && Integer.MIN_VALUE + value2 >= value1)
                        || (value2 < 0 && Integer.MAX_VALUE + value2 <= value1)) {
                    System.err.println("Integer overflow: " + value1 + "-" + value2);
                    System.exit(1);
                }
                values.push(value1 - value2);
            } else if (symbol.equals("*")) {
                operationsChecker(values);
                value2 = values.pop();
                value1 = values.pop();
                if (((value1 > 0 && value2 > 0 || value1 < 0 && value2 < 0) && Integer.MAX_VALUE / value2 <= value1)
                        || ((value1 < 0 && value2 > 0 || value1 > 0 && value2 < 0)
                        && Integer.MIN_VALUE / value2 >= value1)) {
                    System.err.println("Integer overflow: " + value1 + "*" + value2);
                    System.exit(1);
                }
                values.push(value1 * value2);
            } else if (symbol.equals("/")) {
                operationsChecker(values);
                value2 = values.pop();
                value1 = values.pop();
                if (value2 == 0) {
                    System.err.println("Divide by zero");
                    System.exit(1);
                }
                values.push(value1 / value2);
            } else {
                values.push(Integer.parseInt(symbol));
            }
        }
        if (values.size() > 1) {
            System.err.println("Too many variables");
            System.exit(1);
        }
        return values.peek();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: numbers in 17-th system <[0..9, A..G, a..g]>");
            System.out.println("Operations: +, -, *, /");
            System.out.println("Brackets: (, )");
            System.exit(1);
        }
        String expression = "";
        try {
            expression = reversePolishNotationConversation(args);
        } catch (NumberFormatException e) {
            System.err.println(e.getLocalizedMessage() + " - Invalid number");
            System.exit(1);
        }
        try {
            Integer result = calcs(expression);
            System.out.println(Integer.toString(result, RADIX));
        } catch (ArithmeticException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
