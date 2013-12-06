package ru.fizteh.fivt.students.baranov.calculator;

import java.math.BigInteger;
import java.util.Stack;

class Calculator {

    public static Stack<String> stackOfOperations = new Stack<String>();
    public static Stack<String> stackOfNumbers = new Stack<String>();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Hallo, I'm calculator.");
            System.out.println("I can't work without parameters! :C");
            System.out.println("I can calculate arithmetic expression with +, -, *, /, (, ) and integer numbers in 18-system.");
            System.exit(1);
        }
        for (int i = 0; i < args.length; ++i) {
            String currentStr = args[i];
            String num = "";
            int isNumber = 0;
            for (int j = 0; j < currentStr.length(); ++j) {
                char c = currentStr.charAt(j);
                if (Character.toString(c).matches("[0-9A-Ha-h]*")) { 
                    if (isNumber == 1) {
                        num = num + c; 
                    } else {
                        ++isNumber;
                        num = num + c;
                    }
                    if (j == currentStr.length() - 1) {
                        --isNumber;
                        stackOfNumbers.push(num);
                        num = "";
                    }
                } else if (c == '+' || c == '*' || c == '/' || c == '-') {
                    if (isNumber == 1) {
                        --isNumber;
                        stackOfNumbers.push(num);
                        num = "";
                    }
                    if (stackOfOperations.empty()) {
                        stackOfOperations.push(Character.toString(c));
                    } else {
                        while (!stackOfOperations.empty() && priority(stackOfOperations.peek()) >= priority(Character.toString(c))) {
                            makeWork();
                        }
                        stackOfOperations.push(Character.toString(c));
                    }
                } else if (c == '(') {
                    stackOfOperations.push(Character.toString(c));
                    if (isNumber == 1) {
                        --isNumber;
                        stackOfNumbers.push(num);
                        num = "";
                    }
                } else if (c == ')') {
                    if (isNumber == 1) {
                        --isNumber;
                        stackOfNumbers.push(num);
                        num = "";
                    }
                    while (!stackOfOperations.empty() && !stackOfOperations.peek().equals("(")) {
                        if (stackOfOperations.empty()) {
                            System.err.println("Brackets Error");
                            System.exit(1);
                        }
                        makeWork();
                    }
                    if (stackOfOperations.empty()) {
                        System.err.println("Brackets Error");
                        System.exit(1);
                    }
                    stackOfOperations.pop();
                } else if (c == ' ') {
                    if (isNumber == 1) {
                        --isNumber;
                        stackOfNumbers.push(num);
                        num = "";
                    }
                    continue;
                } else {
                    System.err.println("Wrong character");
                    System.exit(1);
                }
            }
        }
        while (!stackOfOperations.empty()) {
            makeWork();
        }
        if (!stackOfNumbers.empty()) {
            String result = stackOfNumbers.pop();
            if (!stackOfNumbers.empty()) {
                System.err.println("Stack of numbers isn't empty after all operations");
                System.exit(1);
            }
            System.out.println(result);
        } else {
            System.err.println("No result :(");
            System.exit(1);
        }
    }

    private static int priority(String s) {
        if (s.equals("+") || s.equals("-")) {
            return 0;
        }
        if (s.equals("*") || s.equals("/")) {
            return 1;
        }
        if (s.equals("(") || s.equals(")")) {
            return -1;
        }
        return 0;
    }

    private static long doOperation(String operation, long y, long x) {
        if (operation.equals("+")) {
            BigInteger result = BigInteger.ZERO;
            BigInteger bigX = BigInteger.valueOf(x);
            BigInteger bigY = BigInteger.valueOf(y);
            result = result.add(bigX);
            result = result.add(bigY);
            if (result.toString().equals(Long.toString(x + y))) {
                return x + y;
            } else {
                System.err.println("Overflow in operation");
                System.exit(1);
            }
        }
        if (operation.equals("-")) {
            BigInteger result = BigInteger.ZERO;
            BigInteger bigX = BigInteger.valueOf(x);
            BigInteger bigY = BigInteger.valueOf(y);
            result = result.add(bigX);
            result = result.subtract(bigY);
            if (result.toString().equals(Long.toString(x - y))) {
                return x - y;
            } else {
                System.err.println("Overflow in operation");
                System.exit(1);
            }
        }
        if (operation.equals("*")) {
            BigInteger result = BigInteger.ZERO;
            BigInteger bigX = BigInteger.valueOf(x);
            BigInteger bigY = BigInteger.valueOf(y);
            result = result.add(bigX);
            result = result.multiply(bigY);
            if (result.toString().equals(Long.toString(x * y))) {
                return x * y;
            } else {
                System.err.println("Overflow in operation");
                System.exit(1);
            }
        }
        if (operation.equals("/")) {
            if (y == 0) {
                System.err.println("Divide on Zero");
                System.exit(1);
            }
            BigInteger result = BigInteger.ZERO;
            BigInteger bigX = BigInteger.valueOf(x);
            BigInteger bigY = BigInteger.valueOf(y);
            result = result.add(bigX);
            result = result.divide(bigY);
            if (result.toString().equals(Long.toString(x / y))) {
                return x / y;
            } else {
                System.err.println("Overflow in operation");
                System.exit(1);
            }
        }
        return 0;
    }

    private static void makeWork() {
        String currentOperation = stackOfOperations.pop();
        if (stackOfNumbers.empty()) {
            System.err.println("Operands Error");
            System.exit(1);
        }
        BigInteger maxLong = BigInteger.valueOf(Long.MAX_VALUE);
        BigInteger negMaxLong = maxLong.negate();
        String strFirst = stackOfNumbers.pop();
        BigInteger bigFirst = new BigInteger(strFirst, 18);

        if (maxLong.compareTo(bigFirst) >= 0 && negMaxLong.compareTo(bigFirst) <= 0) {
            long firstNumber = Long.parseLong(strFirst, 18);

            if (stackOfNumbers.empty()) {
                System.err.println("Operands Error");
                System.exit(1);
            }
            String strSecond = stackOfNumbers.pop();
            BigInteger bigSecond = new BigInteger(strSecond, 18);
            
            if (maxLong.compareTo(bigSecond) >= 0 && negMaxLong.compareTo(bigSecond) <= 0) {
                long secondNumber = Long.parseLong(strSecond, 18);
                long result = doOperation(currentOperation, firstNumber, secondNumber);
                stackOfNumbers.push(Long.toString(result, 18));
            } else {
                System.err.println("Too big number");
                System.exit(1);
            }
        } else {
            System.err.println("Too big number");
            System.exit(1);
        }
    }
}