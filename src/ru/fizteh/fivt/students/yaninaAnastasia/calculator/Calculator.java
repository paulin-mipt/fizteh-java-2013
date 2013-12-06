package ru.fizteh.fivt.students.yaninaAnastasia.calculator;

import java.util.LinkedList;


public class Calculator {
    public static boolean isOperation(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public static int priority(char operation) {
        switch (operation) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    public static void processOperator(LinkedList<Integer> st, char operation) {
        if (st.isEmpty()) {
            System.out.println("Error with operations");
            System.exit(1);
        }
        int right = st.removeLast();
        if (st.isEmpty()) {
            System.out.println("Error with operations");
            System.exit(1);
        }
        int left = st.removeLast();
        switch (operation) {
            case '+':
                if (Integer.MAX_VALUE - left <= right) {
                    System.err.println("Error: integer overflow");
                    System.exit(1);
                }
                st.add(left + right);
                break;
            case '-':
                if (Integer.MIN_VALUE + right >= left) {
                    System.err.println("Error: integer overflow");
                    System.exit(1);
                }
                st.add(left - right);
                break;
            case '*':
                if (left == 0) {
                    st.add(0);
                } else if (Integer.MAX_VALUE / Math.abs(left) <= Math.abs(right)) {
                    System.err.println("Error: integer overflow");
                    System.exit(1);
                }
                st.add(left * right);
                break;
            case '/':
                if (right == 0) {
                    System.err.println("Error: dividing by zero");
                    System.exit(1);
                }
                if (Integer.MIN_VALUE * right >= left) {
                    System.err.println("Error: integer overflow");
                    System.exit(1);
                }
                st.add(left / right);
                break;
            default:
                System.err.println("Unknown operator");
                System.exit(1);
                break;
        }
    }

    public static int count(String s) {
        LinkedList<Integer> st = new LinkedList<Integer>();
        LinkedList<Character> op = new LinkedList<Character>();
        int prevSym = -1;
        int bracketBalance = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            } else if (c == '(') {
                op.add('(');
                prevSym = 3;
                bracketBalance++;
            } else if (c == ')') {
                bracketBalance--;
                while (op.getLast() != '(') {
                    processOperator(st, op.removeLast());
                }
                if (prevSym == 3) {
                    System.err.println("Error: empty brackets");
                    System.exit(1);
                }
                op.removeLast();
            } else if (isOperation(c)) {
                while (!op.isEmpty() && priority(op.getLast()) >= priority(c)) {
                    if (prevSym == 1) {
                        System.err.println("Error with operations");
                        System.exit(1);
                    }
                    processOperator(st, op.removeLast());
                }
                prevSym = 1;
                op.add(c);

            } else {
                StringBuilder sb = new StringBuilder();
                while (i < s.length() && isDigit(s.charAt(i))) {
                    if (prevSym == 2) {
                        System.err.println("Error: too many numbers without operators");
                        System.exit(1);
                    }
                    sb.append(s.charAt(i++));
                }
                --i;
                if (sb.length() == 0) {
                    System.err.println("Error: unknown symbol(s)");
                    System.exit(1);
                }
                prevSym = 2;
                try {
                    st.add(Integer.parseInt(sb.toString(), 19));
                } catch (NumberFormatException e) {
                    System.err.println("Error: integer overflow");
                    System.exit(1);
                }
            }
        }
        while (!op.isEmpty()) {
            if (bracketBalance != 0) {
                System.err.println("Error: wrong bracket balance");
                System.exit(1);
            }
            processOperator(st, op.removeLast());
        }
        return st.get(0);
    }

    private static boolean isDigit(char digit) {
        return Character.toString(digit).matches("[0-9a-iA-I]");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.print("The program has no arguments!");
            System.exit(1);
        }
        StringBuilder expressionBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            expressionBuilder.append(args[i]);
            expressionBuilder.append(" ");
        }
        String expression = expressionBuilder.toString();
        int result = count(expression);
        System.out.print(Integer.toString(result, 19).toUpperCase());
    }
}
