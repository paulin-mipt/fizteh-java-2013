package ru.fizteh.fivt.students.mazanovArtem.calculator;

import java.math.BigInteger;
import java.util.Stack;

public class Calculator {
    static final int RADIX = 19;

    public static void main(String[] args) {
        StringBuilder inputdata = new StringBuilder();
        inputdata.append('(');
        if (args.length == 0) {
            System.out.println("Hello!!!");
            System.exit(0);
        } else {
            for (int i = 0; i < args.length; ++i) {
                if ((i > 0)
                        && (isDigit(args[i - 1]
                                .charAt(args[i - 1].length() - 1)))
                        && (isDigit(args[i].charAt(0)))) {
                    System.out.println("Wrong input data");
                    System.exit(1);
                }
                inputdata.append(args[i]);
            }
            inputdata.append(')');
        }
        try {
            checkData(inputdata.toString());
            inputdata = deleteSpaces(inputdata);
            System.out.println(calc(inputdata));
        } catch (Exception exp) {
            System.err.println(exp.getLocalizedMessage());
            System.exit(1);
        }
    }

    private static boolean isDigit(char k) {
        return ((k >= '0') && (k <= '9') || (k >= 'a') && (k <= RADIX - 10 + 'a'));
    }

    private static void checkData(String input) throws Exception {
        int curChar;
        // 0 = действие,
        // 1 = открывающая скобка
        // 2 = закрывающая скобка
        // 3 = циферка
        // 4 = пробел
        // 5 = что-то левое
        // 6 = нейтральный(начальный)
        int nextchar; // аналогично
        int lastchar = 6;
        int bracketsum = 0;
        for (int i = 0; i < input.length(); ++i) {
            curChar = thatChar(input.charAt(i));
            switch (curChar) {
            case 0:
                if (lastchar == 0) {
                    throw new Exception("Лишний оператор");
                }
                if ((lastchar == 1) && (input.charAt(i) != '-')) {
                    throw new Exception("Неуместный оператор");
                }
                lastchar = curChar;
                break;
            case 1:
                if ((lastchar == 2) || (lastchar == 3)) {
                    throw new Exception("Не хватает оператора");
                }
                bracketsum++;
                lastchar = curChar;
                break;
            case 2:
                if (lastchar == 0) {
                    throw new Exception("Не хватает числа");
                }
                if (lastchar == 1) {
                    throw new Exception(
                            "Что вы хотели написать в пустых скобках?");
                }
                bracketsum--;
                if (bracketsum < 0) {
                    throw new Exception("Неверная скобочная последовательность");
                }
                lastchar = curChar;
                break;
            case 3:
                if (lastchar == 2) {
                    throw new Exception("Не хватает оператора");
                }
                lastchar = curChar;
                break;
            case 4:
                nextchar = thatChar(input.charAt(i + 1)); // за конец строки не
                                                          // вылезем,так как в
                                                          // конце всегда стоит
                                                          // ')'
                if ((lastchar == 3) && (nextchar == 3)) {
                    throw new Exception("Не хватает оператора");
                }
                break;
            case 5:
                throw new Exception("Неизвестные символы");
            default:
                throw new Exception("Unknown error");
            }
        }
        if (bracketsum != 0) {
            throw new Exception("Неверная скобочная последовательность");
        }
    }

    private static StringBuilder deleteSpaces(StringBuilder input) {
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == ' ') {
                input.deleteCharAt(i);
            } else {
                ++i;
            }
        }
        return input;
    }

    private static int thatChar(char k) {
        if (k == '(') {
            return 1;
        }
        if (k == ')') {
            return 2;
        }
        if (isDigit(k)) {
            return 3;
        }
        if ((k == '+') || (k == '-') || (k == '*') || (k == '/')) {
            return 0;
        }
        if (k == ' ') {
            return 4;
        }
        return 5;
    }

    private static String calc(StringBuilder inputdata) throws Exception {
        Stack<String> stack = new Stack<>();
        StringBuilder temp1;
        StringBuilder temp2;
        StringBuilder temp3;
        StringBuilder temp = new StringBuilder();
        BigInteger a;
        BigInteger b;
        int tmp = 0; // для хранения позиции,на которой была последняя
                      // открывающая скобка-чтобы уметь считывать отрицательные
                      // числа
        for (int i = 0; i < inputdata.length(); ++i) {
            if (inputdata.charAt(i) == '(') {
                stack.push("(");
                tmp = i;
            }
            if (isDigit(inputdata.charAt(i))) {
                temp.append(inputdata.charAt(i));
            }
            if (thatChar(inputdata.charAt(i)) == 0) { // проверка на принадлежность оператору
                if ((inputdata.charAt(i) == '-') && (tmp == i - 1)) {
                    temp.append('-');
                    continue;
                } else {
                    if (temp.length() > 0) {
                        stack.push(temp.toString());
                        temp.delete(0, temp.length() - 1);
                    }
                }

                if ((inputdata.charAt(i) == '+')
                        || (inputdata.charAt(i) == '-')) {
                    temp1 = new StringBuilder(stack.pop());
                    temp2 = new StringBuilder(stack.pop());
                    if (temp2.charAt(0) != '(') {
                        temp3 = new StringBuilder(stack.pop());
                        a = new BigInteger(temp1.toString(), RADIX);
                        b = new BigInteger(temp3.toString(), RADIX);
                        switch (temp2.charAt(0)) {
                        case '+':
                            a = a.add(b);
                            stack.push(a.toString(RADIX));
                            break;
                        case '-':
                            a = b.subtract(a);
                            stack.push(a.toString(RADIX));
                            break;
                        case '*':
                            a = a.multiply(b);
                            stack.push(a.toString(RADIX));
                            break;
                        case '/':
                            if (BigInteger.ZERO.equals(a)) {
                                throw new Exception("Divizion by zero");
                            }
                            a = b.divide(a);
                            stack.push(a.toString(RADIX));
                            break;
                        default:
                        }
                    } else {
                        stack.push(temp2.toString());
                        stack.push(temp1.toString());
                    }
                    if (inputdata.charAt(i) == '+') {
                        stack.push("+");
                    } else {
                        stack.push("-");
                    }
                } else {
                    temp1 = new StringBuilder(stack.pop());
                    temp2 = new StringBuilder(stack.pop());
                    if ((temp2.charAt(0) != '(') && (temp2.charAt(0) != '-')
                            && (temp2.charAt(0) != '+')) {
                        temp3 = new StringBuilder(stack.pop());
                        a = new BigInteger(temp1.toString(), RADIX);
                        b = new BigInteger(temp3.toString(), RADIX);
                        switch (temp2.charAt(0)) {
                        case '*':
                            a = a.multiply(b);
                            stack.push(a.toString(RADIX));
                            break;
                        case '/':
                            if (BigInteger.ZERO.equals(a)) {
                                throw new Exception("Divizion by zero");
                            }
                            a = b.divide(a);
                            stack.push(a.toString(RADIX));
                            break;
                        default:
                        }
                    } else {
                        stack.push(temp2.toString());
                        stack.push(temp1.toString());
                    }
                    if (inputdata.charAt(i) == '*') {
                        stack.push("*");
                    } else {
                        stack.push("/");
                    }
                }
                temp.delete(0, temp.length());
            }
            if (inputdata.charAt(i) == ')') {
                if (temp.length() > 0) {
                    stack.push(temp.toString());
                    temp.delete(0, temp.length());
                }
                temp1 = new StringBuilder(stack.pop());
                temp2 = new StringBuilder(stack.pop());
                while (temp2.charAt(0) != '(') {
                    temp3 = new StringBuilder(stack.pop());
                    a = new BigInteger(temp1.toString(), RADIX);
                    b = new BigInteger(temp3.toString(), RADIX);
                    switch (temp2.charAt(0)) {
                    case '+':
                        a = a.add(b);
                        stack.push(a.toString(RADIX));
                        break;
                    case '-':
                        a = b.subtract(a);
                        stack.push(a.toString(RADIX));
                        break;
                    case '*':
                        a = a.multiply(b);
                        stack.push(a.toString(RADIX));
                        break;
                    case '/':
                        if (BigInteger.ZERO.equals(a)) {
                            throw new Exception("Divizion by zero");
                        }
                        a = b.divide(a);
                        stack.push(a.toString(RADIX));
                        break;
                    default:
                    }
                    temp1 = new StringBuilder(stack.pop());
                    temp2 = new StringBuilder(stack.pop());
                }
                stack.push(temp1.toString());
            }
        }
        return stack.pop();
    }
}
