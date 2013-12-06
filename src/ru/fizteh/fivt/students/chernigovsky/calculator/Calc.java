package ru.fizteh.fivt.students.chernigovsky.calculator;

import java.util.ArrayDeque;
import java.io.IOException;
import java.util.Deque;

public class Calc {

    private static final int RADIX = 18;

    private static boolean isDigit(char c) {
        return (c >= '0' && c <= '9' || c >= 'A' && c <= 'H' || c >= 'a' && c <= 'h');
    }

    private static void processOperation(Deque<Integer> numberDeque, char operation) throws IOException {
        if (numberDeque.isEmpty()) {
            throw new IOException("Incorrect input");
        }
        Integer second = numberDeque.peek();
        numberDeque.pop();

        if (numberDeque.isEmpty()) {
            throw new IOException("Incorrect input");
        }
        Integer first = numberDeque.peek();
        numberDeque.pop();

        switch (operation) {
            case '+':
                if ((Integer.signum(first) == Integer.signum(second)) && (Math.abs(first) > Integer.MAX_VALUE - Math.abs(second))) {
                    throw new IOException("Integer overflow");
                }
                numberDeque.push(first + second);
                break;
            case '-':
                if ((Integer.signum(first) != Integer.signum(second)) && (Math.abs(first) > Integer.MAX_VALUE - Math.abs(second))) {
                    throw new IOException("Integer overflow");
                }
                numberDeque.push(first - second);
                break;
            case '*':
                if (Math.abs(first) > Integer.MAX_VALUE / Math.abs(second)) {
                    throw new IOException("Integer overflow");
                }
                numberDeque.push(first * second);
                break;
            case '/':
                numberDeque.push(first / second);
                break;
            default:
                throw new IOException("Incorrect input");
        }
    }

    private static int priority(char operation) {
        if (operation == '*' || operation == '/') {
            return 2;
        } else if (operation == '+' || operation == '-') {
            return 1;
        } else {
            return 0;
        }
    }

    private static int calculateExpression(String expression) throws IOException {
        Deque<Integer> numberDeque = new ArrayDeque<Integer>();
        Deque<Character> operationsDeque = new ArrayDeque<Character>();

        int pos = 0;
        while (pos < expression.length()) {
            if (isDigit(expression.charAt(pos))) {
                int startPos = pos;
                while (isDigit(expression.charAt(pos))) {
                    ++pos;
                }
                numberDeque.push(Integer.parseInt(expression.substring(startPos, pos), RADIX));
            } else {
                switch (expression.charAt(pos)) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                        while (!operationsDeque.isEmpty() && (priority(operationsDeque.peek())) >= priority(expression.charAt(pos))) {
                            processOperation(numberDeque, operationsDeque.peek());
                            operationsDeque.pop();
                        }
                        operationsDeque.push(expression.charAt(pos));
                        break;
                    case '(':
                        operationsDeque.push('(');
                        break;
                    case ')':
                        if (operationsDeque.isEmpty()) {
                            throw new IOException("Incorrect input");
                        }
                        while (operationsDeque.peek() != '(') {
                            processOperation(numberDeque, operationsDeque.peek());
                            operationsDeque.pop();
                            if (operationsDeque.isEmpty()) {
                                throw new IOException("Incorrect input");
                            }
                        }
                        operationsDeque.pop();
                        break;
                    case ' ':
                        break;
                    default:
                        throw new IOException("Incorrect input");
                }

                ++pos;
            }

        }

        while (!operationsDeque.isEmpty()) {
            processOperation(numberDeque, operationsDeque.peek());
            operationsDeque.pop();
        }

        if (numberDeque.size() != 1) {
            throw new IOException("Incorrect input");
        }
        return numberDeque.peek();
    }

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : args) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }
        String expression = stringBuilder.toString();

        try {
            System.out.println(Integer.toString(calculateExpression(expression), RADIX));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (NumberFormatException ex) {
            System.err.println(ex.getMessage());
            System.err.println("The number is too big");
            System.exit(1);
        } catch (ArithmeticException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

}
