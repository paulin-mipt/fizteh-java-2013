package ru.fizteh.fivt.students.valentinbarishev.calculator;

import java.util.Stack;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

    static Stack<String> polskaRecord;

    private static boolean checkSymbolBracket(char c) {
        return ((c == '*') || (c == '-') || (c == '+') || (c == '/'));
    }

    private static boolean checkSymbol(char c) {
        return ((c == '(') || (c == ')') || (checkSymbolBracket(c)));
    }

    private static String getExpression(String[] args) {
        StringBuilder expression = new StringBuilder();

        for (int i = 0; i < args.length; ++i) {
            expression.append(args[i].replace(" ", ""));
            expression.append(" ");
        }

        for (int i = 0; i < expression.length(); ++i) {
            if ((i > 0) && (i < expression.length() - 1) && (expression.charAt(i) == ' ')
                    && (!checkSymbol(expression.charAt(i - 1)) && !checkSymbol(expression.charAt(i + 1)))) {
                throw  new InputMismatchException();
            }
        }

        return expression.toString();
    }

    private static int getPriority(char c) {
        if (c == '(') {
            return 0;
        }
        if (c == ')') {
            return 1;
        }
        if ((c == '+') || (c == '-')) {
            return 2;
        }
        if ((c == '/') || (c == '*')) {
            return 3;
        }
        return 4;
    }

    private static void makePolskaRecord(String expression) {

        polskaRecord = new Stack<String>();
        char[] str = expression.toCharArray();
        Stack<String> digitStack = new Stack<String>();

        for (int i = 0; i < str.length; ++i) {
            if (checkSymbol(str[i])) {
                int priority = getPriority((str[i]));
                if (str[i] == ')') {
                    while (digitStack.lastElement().charAt(0) != '(') {
                        polskaRecord.push(digitStack.pop());
                    }
                    digitStack.pop();
                    continue;
                }

                if ((digitStack.empty()) || (str[i] == '(')) {
                    digitStack.push(Character.toString(str[i]));
                } else {
                    while ((digitStack.size() > 0) && (priority <= getPriority(digitStack.lastElement().charAt(0)))) {
                        polskaRecord.push(digitStack.pop());
                    }
                    digitStack.push(Character.toString(str[i]));
                }
                continue;
            }

            int left = i;
            int right = i;

            while ((right < str.length) && (!checkSymbol(str[right]))) {
                ++right;
            }

            if ((right == str.length) || (checkSymbol(str[right]))) {
                --right;
            }

            polskaRecord.push(expression.substring(left, right + 1));
            i = right;
        }
        while (!digitStack.empty()) {
            polskaRecord.push(digitStack.pop());
        }
    }

    private static long polskaCalculator(int scale) {
        if (!checkSymbol(polskaRecord.lastElement().charAt(0))) {
            Scanner scanner = new Scanner(polskaRecord.pop());
            return scanner.nextLong(scale);
        }
        char c = polskaRecord.pop().charAt(0);
        if (c == '+') {
            long argument2 = polskaCalculator(scale);
            long argument1 = polskaCalculator(scale);

            if (((argument1 >= 0) && (Long.MAX_VALUE - argument1 < argument2))
                    || ((argument1 < 0) && (Long.MIN_VALUE - argument1 > argument2))) {
                throw new ArithmeticException("Overflow while adding numbers.");
            }
            return argument1 + argument2;
        }

        if (c == '-') {
            long argument2 = polskaCalculator(scale);
            long argument1 = polskaCalculator(scale);

            if (((argument1 >= 0) && (Long.MAX_VALUE - argument1 < -argument2))
                    || ((argument1 < 0) && (Long.MIN_VALUE - argument1 > -argument2))) {
                throw new ArithmeticException("Overflow while subtracting numbers.");
            }

            return argument1 - argument2;
        }

        if (c == '*') {
            long argument2 = polskaCalculator(scale);
            long argument1 = polskaCalculator(scale);

            if (((((argument1 > 0) && (argument2 > 0)) || ((argument1 < 0) && (argument2 < 0)))
                    && ((Long.MAX_VALUE / argument1) < argument2))
                    || ((((argument1 < 0) && (argument2 > 0)) || ((argument1 > 0) && (argument2 < 0)))
                    && ((Long.MIN_VALUE / argument1) > argument2))) {
                throw new ArithmeticException("Overflow while multiplication numbers.");
            }

            return argument1 * argument2;
        }

        if (c == '/') {
            long argument2 = polskaCalculator(scale);
            long argument1 = polskaCalculator(scale);

            if (argument2 == 0) {
                throw new ArithmeticException("Division by zero!");
            }

            return argument1 / argument2;
        }

        return 0;
    }

    private static boolean ifCorrectExpression(String expression) {
        char[] str = expression.toCharArray();

        int openBracket = 0;
        for (int i = 0; i < str.length; ++i) {
            if (str[i] == '(') {
                ++openBracket;
                continue;
            }
            if (str[i] == ')') {
                --openBracket;
                if (openBracket < 0) {
                    System.err.println("Wrong bracket sequence.");
                    return false;
                }
                continue;
            }
        }

        if (openBracket != 0) {
            System.err.println("Wrong bracket sequence.");
            return false;
        }

        for (int i = 0; i < str.length; ++i) {
            if ((i > 0) && (str[i] == '(') && (!checkSymbol(str[i - 1]))) {
                System.err.println("Brackets are in incorrect place.");
                return false;
            }
            if ((i + 1 < str.length) && (str[i] == ')') && (!checkSymbol(str[i + 1]))) {
                System.err.println("Brackets are in incorrect place.");
                return false;
            }
            if ((i > 0) && (str[i] == ')') && (checkSymbolBracket(str[i - 1]))) {
                System.err.println("Brackets are in incorrect place.");
                return false;
            }
            if ((i + 1 < str.length) && (str[i] == '(') && (checkSymbolBracket(str[i + 1]))) {
                System.err.println("Brackets are in incorrect place.");
                return false;
            }
        }

        expression = expression.replace("(", "");
        expression = expression.replace(")", "");

        str = expression.toCharArray();
        boolean numberBefore = false;

        for (int i = 0; i < str.length; ++i) {
            if (checkSymbol(str[i])) {
                if (!numberBefore) {
                    System.err.println("Wrong argument.");
                    return false;
                }
                numberBefore = false;
                continue;
            }

            int right = i;

            while ((right < str.length) && (!checkSymbol(str[right]))) {
                ++right;
            }

            if ((right == str.length) || (checkSymbol(str[right]))) {
                --right;
            }

            i = right;
            numberBefore = true;
        }

        if (numberBefore) {
            System.err.println("Wrong argument to last operation.");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Calculator!");
                System.out.println("Input an arithmetic expression!");
                System.out.println("17-radix [0..9,A..G] [+,-,*,/,(,)]");
                System.out.println("Without unary minus!");
                System.exit(1);
            }

            String expression = getExpression(args);
            expression = expression.replace(" ", "");

            if (!ifCorrectExpression(expression)) {
                System.exit(1);
            }

            makePolskaRecord(expression);
            Long answer = polskaCalculator(17);
            System.out.println(Long.toString(answer, 17));

        } catch (InputMismatchException exception) {
            System.err.println("Incorrect number!");
            System.exit(1);
        } catch (ArithmeticException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }
}
