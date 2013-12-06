package ru.fizteh.fivt.students.eltyshev.calc;

import java.io.IOException;

public class ExpressionSolver {
    private static final int RADIX = 17;
    private static final String DIGIT_EXPRESSION = "^-?[0-9a-" + ((char) ('a' + RADIX - 11)) + "]+$";
    public int result;
    private String expression;
    private Node expressionTree;

    public ExpressionSolver(String expression) {
        this.expression = expression;
    }

    public static String execute(String expression) throws IOException{
        ExpressionSolver solver = new ExpressionSolver(expression);
        if (!solver.calculate()) {
            throw new IOException();
        }
        return Integer.toString(solver.result, RADIX);
    }

    public static String formatExpression(String expression) {
        expression = expression.trim();
        if (expression.charAt(0) != '(') {
            return expression;
        }
        int balance = 1;
        for (int index = 1; index < expression.length(); ++index) {
            if (expression.charAt(index) == ')') {
                balance -= 1;
            }
            if (expression.charAt(index) == '(') {
                balance += 1;
            }
            if (balance == 0 && index != expression.length() - 1) {
                return expression;
            }
        }
        return expression.substring(1, expression.length() - 1);
    }

    public static int findOperationSign(String expression) {
        char[][] signs = {new char[]{'-', '+'},
                new char[]{'*', '/'},
                new char[]{'^'}
        };
        for (int i = 0; i < signs.length; ++i) {
            int balance = 0;
            for (int j = expression.length() - 1; j >= 0; j--) {
                char c = expression.charAt(j);
                if (c == '(') {
                    balance++;
                }
                if (c == ')') {
                    balance--;
                }
                if (containsValue(signs[i], c) && balance == 0 && j > 0 && checkPrevious(expression.substring(j - 1, j))) {
                    return j;
                }
            }
        }
        return -1;
    }

    private static boolean containsValue(char[] array, char value) {
        for (char c : array) {
            if (c == value) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkPrevious(String previous) {
        return previous.matches(DIGIT_EXPRESSION) || previous.matches("^[\\)]$") || previous.equals(" ");
    }

    public static boolean checkBracets(String ex) {
        int balance = 0;
        for (int i = 0; i < ex.length() && balance > -1; i++) {
            if (ex.charAt(i) == '(') {
                balance++;
            }
            if (ex.charAt(i) == ')') {
                balance--;
            }
        }
        return balance == 0;
    }

    public static int executeExpression(String operation, int arg1, int arg2) {
        int result = 0;
        if (operation.equals("+")) {
            result = ArithmeticOperationsPerformer.add(arg1, arg2);
        } else if (operation.equals("-")) {
            result = ArithmeticOperationsPerformer.sub(arg1, arg2);
        } else if (operation.equals("*")) {
            result = ArithmeticOperationsPerformer.mul(arg1, arg2);
        } else if (operation.equals("/")) {
            result = ArithmeticOperationsPerformer.div(arg1, arg2);
        } else if (operation.equals("^")) {
            result = ArithmeticOperationsPerformer.pow(arg1, arg2);
        }
        return result;
    }

    public boolean calculate() throws IOException{
        expression = expression.toLowerCase();
        if (!checkBracets(expression)) {
            throw new IOException("Bad bracket balance");
        }

        expressionTree = makeTree(expression);
        executeTree(expressionTree);
        result = expressionTree.Value;
        return true;
    }

    private void executeTree(Node tree) throws IOException{
        if (tree == null) {
            return;
        }

        executeTree(tree.Left);
        executeTree(tree.Right);

        switch (tree.Type) {
            case NUMBER:
                tree.Value = makeNumber(tree.Operation);
                break;
            case OPERATION:
                tree.Value = executeExpression(tree.Operation, tree.Left.Value, tree.Right.Value);
                break;
        }
    }

    private int makeNumber(String expression) throws IOException{
        int result;
        try
        {
            result = Integer.parseInt(expression, RADIX);
        }
        catch (NumberFormatException e)
        {
            throw new IOException(String.format("'%s' is incorrect number!", expression));
        }
        return result;
    }

    private Node makeTree(String ex) throws IOException{
        ex = formatExpression(ex);
        Node res = new Node();
        if (checkNumber(ex)) {
            res.Operation = ex;
            res.Left = null;
            res.Right = null;
            res.Type = ExpressionType.NUMBER;
            return res;
        }

        int signIndex = findOperationSign(ex);
        if (signIndex != -1) {
            res.Operation = ex.substring(signIndex, signIndex + 1);
            res.Left = makeTree(ex.substring(0, signIndex));
            res.Right = makeTree(ex.substring(signIndex + 1));
            res.Type = ExpressionType.OPERATION;
            return res;
        }
        if (ex.equals("")) {
            throw new IOException("An empty expression has been found!");
        }
        throw new IOException("Invalid character: " + ex);
    }

    public boolean checkNumber(String expression) {
        return expression.matches(DIGIT_EXPRESSION);
    }
}
