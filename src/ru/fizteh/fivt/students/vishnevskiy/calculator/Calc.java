package ru.fizteh.fivt.students.vishnevskiy.calculator;

import java.io.*;
import java.text.ParseException;

public class Calc {

    protected static final int RADIX = 19;
    protected static String expression; // выражение
    private static int position;        // текущая обрабатываемая позиция
    private static String token;        // последняя обработанная лексема
    private static int number;          // последнее обработанное число
    private static String tokenType;    // тип лексемы (number, operation, braces, end)

    protected static boolean isDigit(char c) {
        return (Character.isDigit(c)
                || ((Character.toUpperCase(c) >= 'A') && (Character.toUpperCase(c) <= 'A' + RADIX - 11)));
    }

    protected static boolean isInRange(long value) {
        return ((value <= Integer.MAX_VALUE) && (value >= Integer.MIN_VALUE));
    }

    protected static void getToken() {
        while (position < expression.length() && Character.isSpaceChar(expression.charAt(position))) {
            ++position;
        }
        if (position == expression.length()) {
            tokenType = "end";
            return;
        }
        char c = expression.charAt(position);
        if (isDigit(c)) {
            StringBuilder numberTempString = new StringBuilder("");
            while ((position < expression.length()) && (isDigit(expression.charAt(position)))) {
                numberTempString.append(expression.charAt(position));
                ++position;
            }
            String numberString = numberTempString.toString();
            number = Integer.parseInt(numberString, 19);
            tokenType = "number";
            return;
        }
        if ((c == '+') || (c == '-') || (c == '*') || (c == '/')) {
            tokenType = "operation";
            token = String.valueOf(c);
            ++position;
            return;
        }
        if ((c == '(') || (c == ')')) {
            tokenType = "braces";
            token = String.valueOf(c);
            ++position;
            return;
        }
    }

    protected static int prim() throws ParseException {
        if (tokenType.equals("number")) {
            int tempNumber = number;
            getToken();
            return tempNumber;
        } else if (tokenType.equals("operation") && token.equals("-")) {
            getToken();
            return -prim();
        } else if (tokenType.equals("braces") && token.equals("(")) {
            getToken();
            if (tokenType.equals("end")) {
                throw new ParseException("\')\' expected", position);
            }
            int exprValue = expr();
            if (!(tokenType.equals("braces") && token.equals(")"))) {
                throw new ParseException("\')\' expected", position);
            }
            getToken();
            return exprValue;
        }
        throw new ParseException("Primary expected", position);
    }

    protected static int term() throws ArithmeticException, ParseException {
        int leftPrim = prim();
        while (true) {
            if (tokenType.equals("operation") && token.equals("*")) {
                getToken();
                if (tokenType.equals("end")) {
                    return leftPrim;
                }
                int rightPrim = prim();
                long longRightPrim = rightPrim;
                long longLeftPrim = leftPrim;
                if (!isInRange(longLeftPrim * longRightPrim)) {
                    throw new ParseException("impossible to calculate", position);
                }
                leftPrim *= rightPrim;
            } else if (tokenType.equals("operation") && token.equals("/")) {
                getToken();
                if (tokenType.equals("end")) {
                    return leftPrim;
                }
                int rightPrim = prim();
                if (rightPrim != 0) {
                    leftPrim /= rightPrim;
                } else {
                    throw new ArithmeticException("Division by zero");
                }
            } else {
                return leftPrim;
            }
        }
    }

    protected static int expr() throws ArithmeticException, ParseException {
        int leftTerm = term();
        while (true) {
            if (tokenType.equals("operation") && token.equals("+")) {
                getToken();
                if (tokenType.equals("end")) {
                    return leftTerm;
                }
                int rightTerm = term();
                long longRightTerm = rightTerm;
                long longLeftTerm = leftTerm;
                if (!isInRange(longLeftTerm + longRightTerm)) {
                    throw new ParseException("impossible to calculate", position);
                }
                leftTerm += rightTerm;
            } else if (tokenType.equals("operation") && token.equals("-")) {
                getToken();
                if (tokenType.equals("end")) {
                    return leftTerm;
                }
                int rightTerm = term();
                long longRightTerm = rightTerm;
                long longLeftTerm = leftTerm;
                if (!isInRange(longLeftTerm - longRightTerm)) {
                    throw new ParseException("impossible to calculate", position);
                }
                leftTerm -= rightTerm;
            } else {
                return leftTerm;
            }
        }
    }

    protected static int calculate() throws ArithmeticException, ParseException {
        position = 0;
        getToken();
        int answer = expr();
        if (!tokenType.equals("end")) {
            throw new ParseException("Operation expected", position);
        }
        return answer;
    }

    protected static void analyzeCharacters(StringBuilder expression) throws ParseException {
        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);
            if (!isDigit(c) && !Character.isSpaceChar(c)
                    && (c != '(') && (c != ')') && (c != '+') && (c != '-') && (c != '*') && (c != '/')) {
                throw new ParseException("Incorrect input: invalid character \'" + c + "\'", i);
            }
            ++i;
        }
    }

    public static void main(String[] args) {
        if ((args.length == 0) || ((args.length == 1) && (args[0].toLowerCase().equals("help")))) {
            System.out.println(" Input expression as a parameter for Calc. \n You can use integers in 19 radix "
                    + "and operations + - * / (/ stands for integer division). \n "
                    + "Use braces () to set the priority of operations.");
            System.exit(1);
        }
        StringBuilder tempExpression = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            tempExpression.append(args[i]);
            tempExpression.append(' ');
        }
        try {
            analyzeCharacters(tempExpression);
            expression = tempExpression.toString();
            int answer = calculate();
            String strAnswer = Integer.toString(answer, RADIX);
            System.out.println(strAnswer);
        } catch (NumberFormatException e) {
            System.err.println("Incorrect number input");
            System.out.println("Input \"help\" or start the program without parameters to get help.");
            System.exit(1);
        } catch (ArithmeticException e) {
            System.err.println(e);
            System.out.println("Input \"help\" or start the program without parameters to get help.");
            System.exit(1);
        } catch (ParseException e) {
            System.err.println(e);
            System.out.println("Input \"help\" or start the program without parameters to get help.");
            System.exit(1);
        }
    }
}
