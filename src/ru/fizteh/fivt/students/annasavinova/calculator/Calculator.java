package ru.fizteh.fivt.students.annasavinova.calculator;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class Calculator {
    static final int BASE = 18;
    static Stack<String> signStack = new Stack<>();
    static Stack<Long> digitStack = new Stack<>();

    public static int priority(String c) {
        if (c.equals("+") || c.equals("-")) {
            return 0;
        } else if (c.equals("*") || c.equals("/")) {
            return 1;
        } else {
            return -1;
        }
    }

    public static long getVal() {
        if (signStack.size() == 0 || digitStack.size() < 2) {
            System.err.println("Incorrect num of operators and operands");
            System.exit(1);
        }
        String sign = signStack.pop();
        long secondVal = digitStack.pop();
        long firstVal = digitStack.pop();
        long res = 0;
        BigInteger tmpFirstVal = BigInteger.valueOf(firstVal);
        BigInteger tmpSecondVal = BigInteger.valueOf(secondVal);
        BigInteger tmpRes = BigInteger.ZERO;
        switch (sign) {
        case "+":
            tmpRes = tmpFirstVal.add(tmpSecondVal);
            break;
        case "-":
            tmpRes = tmpFirstVal.subtract(tmpSecondVal);
            break;
        case "*":
            tmpRes = tmpFirstVal.multiply(tmpSecondVal);
            break;
        case "/":
            if (secondVal != 0) {
                res = firstVal / secondVal;
            } else {
                System.err.println("Dividing by zero");
                System.exit(1);
            }
            return res;
        default:
            System.exit(1);
        }
        if (tmpRes.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
            System.err.println("Int Overflow");
            System.exit(1);
        }
        if (tmpRes.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
            System.err.println("Int Overflow");
            System.exit(1);
        }
        res = tmpRes.longValue();
        return res;
    }

    public static boolean checkBrackets(String input) {
        int count = 0;
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == '(') {
                ++count;
            } else if (input.charAt(i) == ')') {
                --count;
            }
            if (count < 0) {
                return false;
            }
        }
        if (count != 0) {
            return false;
        }
        return true;
    }

    public static boolean checkSpaces(String str) {
        if (Pattern.matches("[\\s0-9A-H-h/+-/*///(/)]*[0-9A-Ha-h]+[ ]+[0-9A-Ha-h]+[\\s0-9A-Ha-h/+-/*///(/)]*", str)) {
            return false;
        }
        return true;
    }

    public static boolean checkSymbols(String str) {
        if (!Pattern.matches("[\\s0-9A-Ha-h/+-/*///(/)]*", str)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please, input expression, that you want to know");
            System.err.println("Acceptable symbols: +, -, *, /");
            System.err.println("Radix: " + BASE);
            System.exit(1);
        }
        StringBuffer s = new StringBuffer();
        s.append("(");
        for (int i = 0; i < args.length; ++i) {
            s.append(args[i]);
            s.append(" ");
        }
        s.append(")");
        String workingStr = s.toString();
        if (!checkSpaces(workingStr)) {
            System.err.println("Incorrect expression: two operands without operator");
            System.exit(1);
        }
        workingStr = workingStr.replace(" ", "");
        if (!checkBrackets(workingStr)) {
            System.err.println("Incorrect bracket sequance");
            System.exit(1);
        }
        if (!checkSymbols(workingStr)) {
            System.err.println("Incorrect symbol");
            System.exit(1);
        }
        workingStr = workingStr.replace("(", "( ");
        workingStr = workingStr.replace(")", " )");
        workingStr = workingStr.replace("+", " + ");
        workingStr = workingStr.replace("-", " - ");
        workingStr = workingStr.replace("*", " * ");
        workingStr = workingStr.replace("/", " / ");
        Scanner sc = new Scanner(workingStr);
        sc.useRadix(BASE);
        sc.useDelimiter("[ ]+");
        while (sc.hasNext()) {
            if (sc.hasNext("[/(/)/+-/*//]")) {
                String currentSymbol = sc.next();
                switch (currentSymbol) {
                case "(":
                    signStack.push(currentSymbol);
                    break;
                case ")":
                    while (!(signStack.peek()).equals("(")) {
                        digitStack.push(getVal());
                    }
                    if (signStack.size() == 0) {
                        System.err.println("Incorrect num of operators and operands");
                        System.exit(1);
                    }
                    signStack.pop();
                    break;
                default:
                    if (signStack.empty() || priority(signStack.peek()) < priority(currentSymbol)) {
                        signStack.push(currentSymbol);
                    } else {
                        while (priority(signStack.peek()) >= priority(currentSymbol)) {
                            digitStack.push(getVal());
                        }
                        signStack.push(currentSymbol);
                    }
                }
            } else if (sc.hasNextLong()) {
                long currentDigit = sc.nextLong();
                digitStack.push(currentDigit);
            } else {
                sc.close();
                System.err.println("Too big operand");
                System.exit(1);
            }
        }
        if (digitStack.size() == 1) {
            long result = digitStack.pop();
            System.out.println(Long.toString(result, BASE));
        } else {
            System.err.println("Incorrect num of operators and operands");
            sc.close();
            System.exit(1);
        }
        sc.close();
    }
}
