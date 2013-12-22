package ru.fizteh.fivt.students.vlmazlov.calculator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class Calculator {

    private class WrongArithmeticExpressionException extends Exception {
        public WrongArithmeticExpressionException() {
            super();
        }

        public WrongArithmeticExpressionException(String message) {
            super(message);
        }

        public WrongArithmeticExpressionException(String message, Throwable cause) {
            super(message, cause);
        }

        public WrongArithmeticExpressionException(Throwable cause) {
            super(cause);
        }
    }

    private enum OperationType {
        sum("+"),
        sub("-"),
        mult("*"),
        div("/"),
        end("."),
        close(")");

        private String operationType;

        private OperationType(String type) {
            operationType = type;
        }

        public static OperationType getByType(String type) {

            for (OperationType operation : OperationType.values()) {
                if (operation.operationType.equals(type)) {
                    return operation;
                }
            }

            throw new NoSuchElementException("Operation " + type + " is of unknown type");
        }

        public String getType() {
            return operationType;
        }
    }

    private static final int RADIX = 17;
    private String curToken;
    private char curChar;
    private boolean spaceSkipped; //vital for detecting spaces inside an integer
    private InputStream expression;

    Calculator() {
        curToken = "";
    }

    public static void main(String[] args) {
        String arg = "";
        int curRes;
        Calculator calc = new Calculator();

        StringBuilder argBuilder = new StringBuilder();

        for (String tmp : args) {
            argBuilder.append(tmp);
            argBuilder.append(' ');
        }

        arg = argBuilder.toString();

        if (arg.equals("")) {
            System.out.println("Usage: valid aritmetic expression, possibly divided in several strings");
            System.exit(1);
        }

        calc.expression = new ByteArrayInputStream(arg.getBytes());

        try {
            System.out.println(Integer.toString(calc.countExpression(), RADIX).toUpperCase());
        } catch (WrongArithmeticExpressionException ex) {
            System.out.println(ex.getMessage() + ". Please try again.");
            System.exit(2);
        } catch (ArithmeticException ex) {
            System.out.println("Division by zero inside the expression. Please try again.");
            System.exit(3);
        } catch (NoSuchElementException ex) {
            System.out.println(ex.getMessage() + ". Please try again.");
            System.exit(4);
        }
    }

    private boolean isNumber(String toCheck) {
        try {
            int tmp = Integer.parseInt(toCheck, RADIX);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    private boolean isValid() {
        return ((('0' <= curChar) && ('9' >= curChar)) || (('A' <= curChar) && ('A' + RADIX - 10 > curChar)));
    }

    private void parseFail(String errorMessage) throws WrongArithmeticExpressionException {
        throw new WrongArithmeticExpressionException(errorMessage);
    }

    public int countExpression() throws WrongArithmeticExpressionException {
        nextChar();
        nextToken();
        int res = parseExpression();

        if (!curToken.equals(".")) {
            throw new WrongArithmeticExpressionException(curToken + " out of place");
        }

        return res;
    }

    //In all following functions it is assumed that current token
    //is the beginning of the parsed expression

    private int parseExpression() throws WrongArithmeticExpressionException {
        //Expression is considered to be a sequence of summators,
        //connected by + and -

        int res = parseSummator();

        while ((curToken.equals("+")) || (curToken.equals("-"))) {

            int tmpRes;

            OperationType operation = OperationType.getByType(curToken);

            switch (operation) {
                case sum:
                    nextToken();

                    tmpRes = parseSummator();

                    if (((0 < tmpRes) && (Integer.MAX_VALUE - tmpRes < res)) 
                        || ((0 > tmpRes) && (Integer.MIN_VALUE - tmpRes > res))) {
                        parseFail("Arithmetic overflow");
                    }

                    res += tmpRes;

                    break;
                case sub:
                    nextToken();

                    tmpRes = parseSummator();

                    if (((0 > tmpRes) && (Integer.MAX_VALUE + tmpRes < res)) 
                        || ((0 < tmpRes) && (Integer.MIN_VALUE + tmpRes > res))) {
                        parseFail("Arithmetic overflow");
                    }

                    res -= tmpRes;

                    break;
                case end:
                case mult:
                case div:
                case close:
                    return res;
                default:
                    if (operation == OperationType.end) {
                        parseFail("Expression unfinished");
                    } else {
                        parseFail(operation.getType() + " out of place");
                    }
            }
        }
        return res;
    }

    private int parseSummator() throws WrongArithmeticExpressionException {
        //Summator is a sequence of multipliers
        //connected with * or /

        int tmpRes;
        int res = parseMultiplier();

        while ((curToken.equals("*")) || (curToken.equals("/"))) {

            OperationType operation = OperationType.getByType(curToken);

            switch (operation) {
                case mult:
                    nextToken();

                    tmpRes = parseMultiplier();

                    if ((0 != tmpRes) //if tmpRes == 0, overflow is impossible anyway
                            && (Integer.MAX_VALUE / Math.abs(tmpRes) < Math.abs(res))) {
                        parseFail("Arithmetic overflow");
                    }

                    res *= tmpRes;

                    break;
                case div:
                    nextToken();

                    tmpRes = parseMultiplier();

                    if (0 != tmpRes) {

                        res /= tmpRes;

                    } else {
                        throw new ArithmeticException();
                    }

                    break;
                case end:
                case sum:
                case sub:
                case close:
                    return res;
                default:
                    if (operation == OperationType.end) {
                        parseFail("Expression unfinished");
                    } else {
                        parseFail(operation.getType() + " out of place");
                    }
            }
        }
        return res;
    }

    private int parseMultiplier() throws WrongArithmeticExpressionException {
        //Multiplier is either a number or an expression in brackets

        int res = 0;

        if (curToken.equals("(")) {
            nextToken();

            res = parseExpression();

            if (!curToken.equals(")")) {
                parseFail("Unbalanced brackets");
            }

            nextToken();

        } else if (isNumber(curToken)) {
            res = Integer.parseInt(curToken, RADIX);
            nextToken();

        } else if (curToken.equals("-")) {
            nextToken();
            res = -parseMultiplier();

        } else {
            if (curToken.equals(".")) {
                parseFail("Expression unfinished");
            } else {
                parseFail(curToken + " out of place");
            }
        }

        return res;
    }

    private void nextChar() {
        try {
            if (0 < expression.available()) {
                curChar = (char) expression.read();
            } else {
                curChar = '.';
            }
        } catch (IOException ex) {
            curChar = '.';
        }
        if (' ' == curChar) {
            spaceSkipped = true;
            nextChar();
        }
    }

    //Valid tokens:
    // (,),+,-,/,*,., string representing a number
    //. - end of expression

    private void nextToken() throws WrongArithmeticExpressionException {
        //nextToken is never called recursively, therefore, it's valid
        spaceSkipped = false;

        switch (curChar) {
            case '(':
            case ')':
            case '+':
            case '-':
            case '/':
            case '*':
            case '.':
                char[] curCharArray = {curChar};
                curToken = new String(curCharArray);
                nextChar();
                break;
            default:
                if (!isValid()) {
                    parseFail("Unknown symbol " + curChar);
                }

                StringBuilder curTokenBuilder = new StringBuilder();

                while ((isValid()) && (!spaceSkipped)) {
                    curTokenBuilder.append(curChar);
                    nextChar();
                }

                spaceSkipped = false;

                curToken = curTokenBuilder.toString();
        }
    }
}

