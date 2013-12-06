package ru.fizteh.fivt.students.nadezhdakaratsapova.calculator;

import java.io.IOException;
import java.util.Stack;

public class Calculator {

    public static int priority(final char token) {
        switch (token) {
            case '*':
            case '/':
                return 2;

            case '+':
            case '-':
                return 1;
            default:
                break;
        }
        return 0;
    }

    public static Integer calculate(final Integer arg1, final Integer arg2, final char operation) throws IOException {
        switch (operation) {
            case '*':
                if ((arg1 == 0) | (arg2 == 0)) {
                    return 0;
                }
                if ((Math.abs(Integer.MAX_VALUE / arg1)) < Math.abs(arg2)) {
                    throw new IOException("Value of expression exceeds  MAX_INTEGER1");
                }
                return arg1 * arg2;
            case '/':
                if (arg2 == 0) {
                    throw new IOException("Division by zero");
                }
                return arg1 / arg2;
            case '+':
                if (Math.max(arg1, arg2) > 0) {
                    if ((Integer.MAX_VALUE - arg1) < arg2) {
                        throw new IOException("Value of expression exceeds  MAX_INTEGER");
                    }
                } else {
                    if ((Integer.MIN_VALUE + arg1) > arg2) {
                        throw new IOException("Value of expression less than  MIN_INTEGER");
                    }
                }
                return arg1 + arg2;
            case '-':
                if ((arg1 < 0) && (arg1 < (Integer.MIN_VALUE + arg2))) {
                    throw new IOException("Value of expression less than MIN_INTEGER");
                }
                if ((arg2 < 0) && (arg1 > (Integer.MAX_VALUE + arg2))) {
                    throw new IOException("Value of expression exceeds  MAX_INTEGER");
                }
                return arg1 - arg2;
            default:
                break;
        }
        return 0;
    }


    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("There are no arguments;");
                System.out.println("Calculator supports four operations: + , - , * , /");
                System.out.println("Numbers: 0 - 9, A - G;");
                System.out.println("Minus number should be in brackets");
                System.out.println("Example of correct expression: 4 + 5 * (6 - 7/(-3))");
                System.exit(2);
            }
            StringBuilder algExpression = new StringBuilder(args[0]);
            for (int i = 1; i < args.length; ++i) {
                algExpression.append(' ');
                algExpression.append(args[i]);
            }
            String task = new String(algExpression);
            int radix = 17;
            StringBuilder outputString = new StringBuilder();
            Stack<Character> dataStack = new Stack<Character>();
            int indexBegin;
            int indexEnd;
            int i = 0;
            boolean flagOpen = false;
            //знака минус после скобки нет (для обработки отрицательных чисел);
            boolean flagClose = false;
            int prevToken = 1;       //1 - арифметичесая операция;
            //2 - число;
            // 3 - скобка;
            while (i < task.length()) {
                indexBegin = i;
                while ((i < task.length()) && ((Character.isDigit(task.charAt(i)))
                        | ((task.charAt(i) >= 'A') && (task.charAt(i) <= 'G')))) {
                    ++i;
                }
                if (indexBegin != i) {
                    if (prevToken == 2) {
                        throw new IOException("Operation was missed");
                    } else {
                        indexEnd = i;
                        if (!flagClose) {
                            outputString.append(task.substring(indexBegin, indexEnd));
                            outputString.append(' ');
                            --i;
                        } else {
                            throw new IOException("Minus have to be in brackets");
                        }
                        if (flagOpen) {
                            flagClose = true;
                        }
                        prevToken = 2;
                    }
                } else {
                    if ((task.charAt(i) == '(')) {
                        if (prevToken != 2) {
                            flagClose = false;
                            if (((i + 2) != task.length()) && (task.charAt(i + 1) == '-')) {
                                if (task.charAt(i + 2) == '(') {
                                    dataStack.push('*');
                                    outputString.append("-1");
                                    outputString.append(' ');
                                    dataStack.push(task.charAt(i));
                                    ++i;
                                } else {
                                    outputString.append('-');
                                    dataStack.push(task.charAt(i));
                                    ++i;
                                    flagOpen = true;
                                }
                            } else {
                                dataStack.push(task.charAt(i));
                            }
                        } else {
                            throw new IOException("Operation was missed");
                        }
                        prevToken = 3;
                    } else {
                        if (task.charAt(i) == ')') {
                            if (prevToken != 1) {
                                char top;
                                if (flagOpen && !flagClose) {
                                    throw new IOException("Number was missed");
                                }
                                flagClose = false;
                                flagOpen = false;
                                while ((!(dataStack.empty())) && ((top = dataStack.peek()) != '(')) {
                                    outputString.append(top);
                                    outputString.append(' ');
                                    dataStack.pop();
                                }
                                if (!dataStack.empty()) {
                                    dataStack.pop();
                                } else {
                                    throw new IOException("Not enough brackets");
                                }
                            } else {
                                throw new IOException("Number was missed");
                            }
                            prevToken = 3;
                        } else {
                            int pr = priority(task.charAt(i));
                            if (pr > 0) {
                                if (prevToken != 1) {
                                    while ((!dataStack.empty()) && (pr <= (priority(dataStack.peek())))) {
                                        outputString.append(dataStack.pop());
                                        outputString.append(' ');
                                    }
                                    dataStack.push(task.charAt(i));
                                } else {
                                    throw new IOException("Number was missed");
                                }
                                prevToken = 1;
                            } else {
                                if ((task.charAt(i) != ' ') && (task.charAt(i) != '\t')) {
                                    throw new IOException("Undefined symbol");
                                }
                            }
                        }
                    }

                }
                ++i;
            }
            while (!dataStack.empty()) {
                char top = dataStack.peek();
                if ((top == '(') | (top == ')')) {
                    throw new IOException("Not enough brackets");
                } else {
                    outputString.append(dataStack.pop());
                    outputString.append(' ');
                }

            }
            Stack<Integer> result = new Stack<Integer>();
            i = 0;
            while (i < (outputString.length() - 1)) {
                if (outputString.charAt(i + 1) == ' ') {
                    if ((Character.isDigit(outputString.charAt(i)))
                            | ((outputString.charAt(i) >= 'A') && (outputString.charAt(i) <= 'G'))) {
                        result.push(Integer.parseInt(outputString.substring(i, i + 1), radix));
                    } else {
                        if (!result.empty()) {
                            Integer arg2 = result.pop();
                            Integer arg1;
                            if (!result.empty()) {
                                arg1 = result.pop();
                            } else {
                                throw new IOException("Number was missed");
                            }
                            result.push(calculate(arg1, arg2, outputString.charAt(i)));
                        } else {
                            throw new IOException("Number was missed");
                        }
                    }
                    ++i;
                } else {
                    indexBegin = i;
                    while (outputString.charAt(i) != ' ') {
                        ++i;
                    }
                    indexEnd = i;
                    String maxInteger = Integer.toString(Integer.MAX_VALUE);
                    if ((indexEnd - indexBegin) > maxInteger.length()) {
                        throw new IOException("Too big argument");
                    }
                    if ((indexEnd - indexBegin) == maxInteger.length()) {
                        int k = 0;
                        for (int j = indexBegin; j < indexEnd; ++j, ++k) {
                            if (outputString.charAt(j) > maxInteger.charAt(k)) {
                                throw new IOException("Too big argument");
                            }
                        }
                    }
                    result.push(Integer.parseInt(outputString.substring(indexBegin, indexEnd), radix));
                }
                ++i;
            }
            if (result.empty()) {
                throw new IOException("Number was missed");
            }
            int res = result.pop();
            if (result.empty()) {
                System.out.println(Integer.toString(res, radix));
            } else {
                throw new IOException("Operation was missed");
            }
        } catch (IOException e) {
            System.err.println("Exception was caught: " + e.getMessage());
            System.exit(1);
        }
    }
}



