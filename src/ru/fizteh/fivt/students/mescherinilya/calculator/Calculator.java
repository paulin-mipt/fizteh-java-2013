package ru.fizteh.fivt.students.mescherinilya.calculator;

import java.util.ArrayDeque;
import java.util.Deque;

public class Calculator {

    private static final int BASE = 18; //must be less than or equal to 10

    private static boolean isOp(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private static int priority(int op) {
        if (op < 0) {
            return 4;
        }
        return
                op == '+' || op == '-' ? 1
                        : op == '*' || op == '/' || op == '%' ? 2
                        : -1;
    }

    private static void executeOperation(Deque<Integer> numbers, Integer op) {
        if (op < 0) {
            Integer operand = numbers.pop();
            switch (-op) {
                case (int) '+':
                    numbers.push(operand);
                    break;
                case (int) '-':
                    numbers.push(-operand);
                    break;
                default:
            }
        } else {
            Integer right = numbers.pop();
            Integer left = numbers.pop();
            switch (op) {
                case (int) '+':
                    if ((left > 0 && right > 0 || left < 0 && right < 0)
                            && Math.abs(left) > Math.abs(Integer.MAX_VALUE - right)) {
                        System.err.println("Error: overflow");
                        System.exit(1);
                    }
                    numbers.push(left + right);
                    break;
                case (int) '-':
                    if ((left < 0 && right > 0 || left > 0 && right < 0)
                            && Math.abs(left) > Math.abs(Integer.MAX_VALUE - right)) {
                        System.err.println("Error: overflow");
                        System.exit(1);
                    }
                    numbers.push(left - right);
                    break;
                case (int) '*':
                    if (right != 0 && Math.abs(left) > Math.abs(Integer.MAX_VALUE / right)) {
                        System.err.println("Error: overflow");
                        System.exit(1);
                    }
                    numbers.push(left * right);
                    break;
                case (int) '/':
                    if (right == 0) {
                        System.err.println("Error: division by zero");
                        System.exit(1);
                    }
                    numbers.push(left / right);
                    break;
                case (int) '%':
                    if (right == 0) {
                        System.err.println("Error: division by zero");
                        System.exit(1);
                    }
                    numbers.push(left % right);
                    break;
                default:
            }
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.err.println("Using: Calculator <expression>");
            System.exit(1);
        }

        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s);
            builder.append(' ');
        }

        String str = builder.toString();

        //проверим корректность выражения
        boolean thereWasNumber = false;
        boolean thereWasOp = false;
        for (int i = 0; i < str.length(); i++) {
            char cur = str.charAt(i);
            char next = (i < str.length() - 1 ? str.charAt(i + 1) : 0);
            if (!Character.isWhitespace(cur) && !Character.isDigit(cur) && (cur < 'A' || cur > 'A' + BASE - 11)
                    && !isOp(cur) && str.charAt(i) != '(' && str.charAt(i) != ')') {
                System.err.println("Error: unknown symbol or unsupported operation");
                System.exit(1);
            }
            if (i < str.length() - 1
                    && (Character.isWhitespace(cur) || cur == '(' || cur == ')')
                    && (!Character.isWhitespace(next) && next != '(' && next != ')')) {
                if (isOp(next) && thereWasOp && (cur != '(' || next != '-')) {
                    System.err.println("Error: two operators in a row!");
                    System.exit(1);
                }
                if ((Character.isDigit(next) || next >= 'A' && next <= 'A' + BASE - 11) && thereWasNumber) {
                    System.err.println("Error: two numbers in a row!");
                    System.exit(1);
                }
            }
            if (i < str.length() - 1 && isOp(cur) && isOp(next)) {
                System.err.println("Error: two operators in a row!");
                System.exit(1);
            }

            if (!thereWasNumber && !thereWasOp && isOp(str.charAt(i)) && str.charAt(i) != '-') {
                System.err.println("Error: a binary operator in the beginning of expression!");
                System.exit(1);
            }


            if (Character.isDigit(str.charAt(i)) || str.charAt(i) >= 'A' && str.charAt(i) <= 'A' + BASE - 11) {
                thereWasNumber = true;
                thereWasOp = false;
            }
            if (isOp(str.charAt(i))) {
                thereWasOp = true;
                thereWasNumber = false;
            }

            if (thereWasNumber && str.charAt(i) == '(') {
                System.err.println("Error: a number before an opening bracket!");
                System.exit(1);
            }
            if (thereWasOp && str.charAt(i) == ')') {
                System.err.println("Error: a closing bracket after an operation mark!");
                System.exit(1);
            }
            if (thereWasOp && i == str.length() - 1) {
                System.err.println("Error: a binary operator in the end of expression!");
                System.exit(1);
            }
        }


        int bracketsBalance = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                bracketsBalance++;
            } else if (str.charAt(i) == ')') {
                bracketsBalance--;
            }
            if (bracketsBalance < 0) {
                break;
            }
        }
        if (bracketsBalance != 0) {
            System.err.println("Error: incorrect brackets arrangement");
            System.exit(1);
        }


        boolean mayUnary = true;
        ArrayDeque<Integer> numbers = new ArrayDeque<Integer>();
        ArrayDeque<Integer> operations = new ArrayDeque<Integer>();


        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                if (str.charAt(i) == '(') {
                    operations.push((int) '(');
                    mayUnary = true;
                } else if (str.charAt(i) == ')') {
                    while (operations.peek() != '(') {
                        executeOperation(numbers, operations.peek());
                        operations.pop();
                    }
                    operations.pop();
                    mayUnary = false;
                } else if (isOp(str.charAt(i))) {
                    char c = str.charAt(i);
                    int curOp = c;
                    if (mayUnary && (curOp == '+' || curOp == '-')) {
                        curOp = -curOp;
                    }
                    while (!operations.isEmpty() && priority(operations.peek()) >= priority(curOp)) {
                        executeOperation(numbers, operations.pop());
                    }
                    operations.push(curOp);
                    mayUnary = true;
                } else {
                    String operand = "";
                    while (i < str.length()
                            && (Character.isDigit(str.charAt(i))
                            || str.charAt(i) >= 'A' && str.charAt(i) <= 'A' + BASE - 11)) {
                        operand += str.charAt(i++);
                    }
                    i--;
                    try {
                        numbers.push(Integer.parseInt(operand.toString(), BASE));
                    } catch (NumberFormatException exception) {
                        System.err.println("Error: overflow");
                        System.exit(1);
                    }
                    mayUnary = false;
                }
            }
        }
        while (!operations.isEmpty()) {
            executeOperation(numbers, operations.pop());
        }


        if (!numbers.isEmpty()) {
            System.out.println(Integer.toString(numbers.pop(), BASE));
        } else {
            System.err.println("Incorrect expression.");
            System.exit(1);
        }

    }

}
