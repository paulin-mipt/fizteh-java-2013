package ru.fizteh.fivt.students.vyatkina.calc;

import java.util.Scanner;
import java.util.Stack;

public class StackCalculator {

    private Stack<Integer> numberStack = new Stack<Integer>();
    private Stack<Character> operantionStack = new Stack<Character>();
    static final int RADIX = 19;

    boolean nextIsNumber = true;

    int calculateExpression(String expression) throws IllegalArgumentException {
        Scanner scanner = new Scanner("(" + expression.replace(" ", "") + ")");


        String bracket = "\\(|\\)";
        String operand = "\\+|\\-|\\*|\\/";

        scanner.useDelimiter("((?<=\\w)(?=\\p{Punct})|(?<=\\p{Punct})(?=\\w)|(?<=\\p{Punct})(?=\\p{Punct}))");
        scanner.useRadix(RADIX);

        while (scanner.hasNext()) {
            if (scanner.hasNext(bracket)) {
                handleBrackets(scanner.next(bracket).charAt(0));
            } else if (expectedNumber() && scanner.hasNextInt()) {
                handleNumber(scanner.nextInt());
                changeExpectation();
            } else if (expectedOperand() && scanner.hasNext(operand)) {
                handleOperand(scanner.next(operand).charAt(0));
                changeExpectation();
            } else {
                throw new IllegalArgumentException("Unexpected token: [" + scanner.next()
                        + "]");
            }
        }
        scanner.close();

        return getResult();
    }

    private void handleOperand(char operand) {
        while (!operantionStack.empty()
                && (priority(operantionStack.peek()) >= priority(operand))) {
            moveTheLastOperation();
        }
        operantionStack.push(operand);
    }

    private int priority(char operand) {
        int result = -1;

        if ((operand == '+') || (operand == '-')) {
            result = 0;
        } else if ((operand == '*') || (operand == '/')) {
            result = 1;
        }
        return result;
    }

    private void handleNumber(int number) {
        numberStack.push(number);
    }

    private void handleBrackets(char operand) {
        if (operand == '(') {
            operantionStack.push(operand);
        } else if (operand == ')') {
            // pop everything out of stack until find the '('
            while (operantionStack.peek() != '(') {
                moveTheLastOperation();
            }
            operantionStack.pop();
        }
    }

    private void moveTheLastOperation() throws IllegalArgumentException {
        int a = numberStack.pop();
        int b = numberStack.pop();

        int result = 0;
        char operand = operantionStack.pop();
        ExpressionCheck.operationOverflowCheck(b, a, operand);

        switch (operand) {
            case '+': {
                result = b + a;
                break;
            }
            case '-': {
                result = b - a;
                break;
            }
            case '*': {
                result = b * a;
                break;
            }
            case '/': {
                result = b / a;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unexpected token [" + operand + "]");
            }
        }

        numberStack.push(result);

    }

    boolean expectedNumber() {
        return nextIsNumber;
    }

    boolean expectedOperand() {
        return !nextIsNumber;
    }

    void changeExpectation() {
        nextIsNumber = !nextIsNumber;
    }

    int getResult() {
        return numberStack.pop();
    }

    public static void main(String[] args) {

        if (args.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb.append(s);
                sb.append(" ");
            }
            String expression = sb.toString();
            try {
                ExpressionCheck.bracketBalanceCheck(expression);
                ExpressionCheck.invalidExpressionCheck(expression);

                StackCalculator sc = new StackCalculator();

                int result = sc.calculateExpression(expression);
                System.out.println(Integer.toString(result, sc.RADIX));
            }
            catch (IllegalArgumentException iae) {
                System.err.println(iae.getMessage());
                System.exit(1);
            }
            System.exit(0);
        } else {
            System.out.println("This is Calculator. Give it arguments like '16 - 3 * (5 + 5)'");
            System.exit(1);
        }

    }

}
