package ru.fizteh.fivt.students.inaumov.calculator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Stack;

public class Analysis  {
    public static final int RADIX = 17;
    private Stack<Integer> integerStack;
    private Stack<Character> operationStack;
    private String inputData;

    public Analysis(final String inputString) {
        this.inputData = inputString;
        integerStack = new Stack<Integer>();
        operationStack = new Stack<Character>();
    }

    private int getIntegerFromStack() throws IOException {
        if (integerStack.isEmpty()) {
            throw new IOException();
        }
        return integerStack.pop();
    }

    private char getOperationFromStack() throws IOException {
        if (operationStack.isEmpty()) {
            throw new IOException();
        }
        return operationStack.pop();
    }

    public static int getPriority(char c) {
        switch (c) {
            case '(':
                return 0;
            case ')':
                return 1;
            case '+':
            case '-':
                return 2;
            case '*':
            case '/':
                return 3;
            default:
                return -1;
        }
    }

    public static boolean isOperation(char c) {
        if (c == '+' || c == '-' || c == '*' || c == '/') {
            return true;
        }
        return false;
    }

    public int calculateExpression() throws IOException {
        char nextOperation = getOperationFromStack();
        int secondOperand = getIntegerFromStack();
        int firstOperand = getIntegerFromStack();
        int result = 0;
        if (nextOperation == '+') {
            if (secondOperand <= 0 && firstOperand >= 0
                || secondOperand >= 0 && firstOperand <= 0
                || Integer.MAX_VALUE - Math.abs(secondOperand) >= Math.abs(firstOperand)) {
                result = firstOperand + secondOperand;
            } else {
                throw new IOException();
            }
        } else if (nextOperation == '-') {
            if (secondOperand >= 0 && firstOperand >= 0
                || secondOperand <= 0 && firstOperand <= 0
                || Integer.MAX_VALUE - Math.abs(secondOperand) >= Math.abs(firstOperand)) {
                result = firstOperand - secondOperand;
            } else {
                throw new IOException();
            }
        } else if (nextOperation == '*') {
            if (secondOperand == 0 || Integer.MAX_VALUE / Math.abs(secondOperand) >= Math.abs(firstOperand)) {
                result = firstOperand * secondOperand;
            } else {
                throw new IOException();
            }
        } else if (nextOperation == '/') {
            if (secondOperand != 0) {
                result = firstOperand / secondOperand;
            } else {
                throw new IOException();
            }
        } else {
            throw new IOException();
        }
        return result;
    }

    public int calculateAnswer()  throws IOException {
        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
        Character nextChar = 0;
        String integerInString = new String();
        int result = 0;
        while (true) {
            int bufReaderRetVal = bufReader.read();
            if (bufReaderRetVal == -1) {
                if (!integerInString.isEmpty()) {
                    integerStack.push(Integer.parseInt(integerInString, RADIX));
                    integerInString = "";
                }
                break;
            }
            nextChar = (char) bufReaderRetVal;
            if (nextChar >= '0' && nextChar <= '9'
                || nextChar >= 'a' && nextChar <= 'a' + RADIX - 11
                || nextChar >= 'A' && nextChar <= 'A' + RADIX - 11) {
                integerInString += nextChar;
            } else {
                if (!integerInString.isEmpty()) {
                    integerStack.push(Integer.parseInt(integerInString, RADIX));
                    integerInString = "";
                }
                if (nextChar == '(') {
                    operationStack.push(nextChar);
                } else if (nextChar == ')') {
                    while (!operationStack.empty() && operationStack.peek() != '(') {
                        result = calculateExpression();
                        integerStack.push(result);
                    }
                    if (!operationStack.empty() && operationStack.peek() == '(') {
                        operationStack.pop();
                    } else {
                        throw new IOException();
                    }
                } else {
                    if (isOperation(nextChar)) {
                        while (!operationStack.empty()
                            && getPriority(operationStack.peek()) >= getPriority(nextChar)) {
                            result = calculateExpression();
                            integerStack.push(result);
                        }
                        operationStack.push(nextChar);
                    } else if (nextChar != ' ') {
                        throw new IOException();
                    }
                }
            }
        }
        while (!operationStack.empty()) {
            result = calculateExpression();
            integerStack.push(result);
        }
        if (integerStack.empty() || integerStack.size() > 1) {
            throw new IOException();
        } else {
            result = integerStack.pop();
        }
        return result;
    }
}
