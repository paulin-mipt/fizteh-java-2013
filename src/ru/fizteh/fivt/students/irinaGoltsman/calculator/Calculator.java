package ru.fizteh.fivt.students.irinaGoltsman.calculator;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

public class Calculator {

    private static boolean isItArithmeticSign(String c) {
        return (c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/"));
    }

    //Проверяет, является ли введённый символ цифрой.
    private static boolean isItDigit(char c) {
        return ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'G'));
    }

    private static boolean isNumber(String number) {
        switch (number.length()) {
            case 0:
                return false;
            case 1:
                return isItDigit(number.charAt(0));
            default: {
                for (int i = 1; i < number.length(); i++) {
                    if (!isItDigit(number.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    private static ArrayList<String> divideNumberMinusNumber(String string) {
        ArrayList<String> result = new ArrayList<>();
        boolean flag = false;
        StringBuilder tmp = new StringBuilder();
        char c;
        for (int i = 0; i < string.length(); i++) {
            c = string.charAt(i);
            if (isItDigit(c)) {
                tmp.append(c);
                flag = false;
            } else if (c == '-') {
                if (!flag) {
                    flag = true;
                    result.add(tmp.toString());
                    tmp.delete(0, tmp.length());
                    result.add("-");
                } else {
                    result.clear();
                    return result;
                }
            } else {
                result.clear();
                return result;
            }
        }
        if (tmp.length() != 0) {
            result.add(tmp.toString());
        }
        return result;
    }

    //Проверка введённого выражения на корректность.
    public enum status {
        OPENING_BRACKET,
        CLOSING_BRACKET,
        ARITHMETIC_SYMBOL,
        NUMBER,
        ERROR
    }


    private static void checkExpression(ArrayList<String> list) {
        if (list.size() == 0) {
            System.err.println("You have tried to run the program 'Calculator'");
            System.err.println("Please, restart it with expression that you want to count");
            System.err.println("Use symbols: '-', '+', '/', '*', ')', '(',");
            System.err.println("numbers : [0..9], and letters: [A..G]");
            System.exit(1);
        } else {
            status statusOfLastSymbol;
            //Разница между количеством открывающих и закрывающих скобок
            int brackets = 0;
            //Рассматриваемый символ
            int start = 0;
            if (list.get(start).equals("(")) {
                brackets++;
                //Тип последнего рассмотренного символа
                statusOfLastSymbol = status.OPENING_BRACKET;
            } else if (isNumber(list.get(start))) {
                statusOfLastSymbol = status.NUMBER;
            } else {
                ArrayList<String> buf;
                buf = divideNumberMinusNumber(list.get(start));
                if (buf.size() == 0) {
                    System.err.println("Incorrect input:" + list.get(start) + " Check the begin of the formula.");
                    System.exit(1);
                }
                list.remove(start);
                list.addAll(start, buf);
                start = buf.size() - 1;
                if (list.get(start).equals("-")) {
                    statusOfLastSymbol = status.ARITHMETIC_SYMBOL;
                } else {
                    statusOfLastSymbol = status.NUMBER;
                }
            }
            for (int i = start + 1; i < list.size(); i++) {
                if (isNumber(list.get(i))) {
                    if (statusOfLastSymbol == status.CLOSING_BRACKET) {
                        System.err.println("Incorrect input: after ')' can not be a number.");
                        System.exit(1);
                    }
                    if (statusOfLastSymbol == status.NUMBER) {
                        System.err.println("Incorrect input: after a number can not be another number.");
                        System.exit(1);
                    }
                    statusOfLastSymbol = status.NUMBER;
                } else if (list.get(i).equals("(")) {
                    brackets++;
                    if (statusOfLastSymbol == status.CLOSING_BRACKET) {
                        System.err.println("Incorrect input: after ')' can not be '('.");
                        System.exit(1);
                    }
                    if (statusOfLastSymbol == status.NUMBER) {
                        System.err.println("Incorrect input: after a number can not be '('  .");
                        System.exit(1);
                    }
                    statusOfLastSymbol = status.OPENING_BRACKET;
                } else if (list.get(i).equals(")")) {
                    brackets--;
                    if (statusOfLastSymbol == status.OPENING_BRACKET) {
                        System.err.println("Incorrect input: after '(' can not be ')'.");
                        System.exit(1);
                    }
                    if (statusOfLastSymbol == status.ARITHMETIC_SYMBOL) {
                        System.err.println("Incorrect input: after an arithmetic sign can not be ')'.");
                        System.exit(1);
                    }
                    statusOfLastSymbol = status.CLOSING_BRACKET;
                } else if (isItArithmeticSign(list.get(i))) {
                    if (statusOfLastSymbol == status.OPENING_BRACKET) {
                        System.err.println("Incorrect input: after '(' can not be an arithmetic sign.");
                        System.exit(1);
                    }
                    if (statusOfLastSymbol == status.ARITHMETIC_SYMBOL) {
                        System.err.println("Incorrect input: there can not be two arithmetic signs together.");
                        System.exit(1);
                    }
                    statusOfLastSymbol = status.ARITHMETIC_SYMBOL;
                } else {
                    ArrayList<String> buf;
                    buf = divideNumberMinusNumber(list.get(i));
                    if (buf.size() == 0) {
                        System.err.println("Incorrect input:" + list.get(i));
                        System.exit(1);
                    }
                    list.remove(i);
                    list.addAll(i, buf);
                    i += buf.size() - 1;
                    if (list.get(i).equals("-")) {
                        statusOfLastSymbol = status.ARITHMETIC_SYMBOL;
                    } else {
                        statusOfLastSymbol = status.NUMBER;
                    }
                }
            }
            if (statusOfLastSymbol == status.ARITHMETIC_SYMBOL) {
                System.err.println("Incorrect input: the expression can not end on an arithmetic sign ");
                System.exit(1);
            }
            if (brackets != 0) {
                System.err.println("Incorrect input: there is a wrong number of brackets");
                System.exit(1);
            }
        }
    }

    //Приоритет операций
    private static int priority(String a) {
        int p;
        if (a.equals("(")) {
            p = 0;
        } else if (a.equals(")")) {
            p = 1;
        } else if (a.equals("+") || a.equals("-")) {
            p = 2;
        } else if (a.equals("*") || a.equals("/")) {
            p = 3;
        } else {
            p = -1;
        }
        return p;
    }

    private static void polishRecord(ArrayList<String> list) {
        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        int priorPrev;
        int prior;
        for (String string : list) {
            if (isNumber(string)) {
                result.add(string);
            } else {
                if (stack.size() == 0) {
                    priorPrev = 0;
                } else {
                    priorPrev = priority(stack.peek());
                }
                //приоритет последнего элемента в стеке
                prior = priority(string);        //приоритет текущего элемента
                //если в стеке нет операций или верхним элементом стека является открывающая скобка,
                // операции кладётся в стек;
                if ((stack.size() == 0 || priorPrev == 0) && prior != 1) {
                    stack.push(string);
                    continue;
                }
                if (prior == 0) {                    //открвающая скобка кладется в стек
                    stack.push(string);
                    continue;
                }
                //До тех пор, пока верхним элементом стека не станет открывающая скобка,
                // выталкиваем элементы из стека в выходную строку
                if (prior == 1) {
                    while (priorPrev != 0) {
                        result.add(stack.pop());
                        if (stack.size() == 0) {
                            priorPrev = 0;
                        } else {
                            priorPrev = priority(stack.peek());
                        }
                    }
                    stack.pop();            //delete last elem
                    continue;
                }
                //если новая операции имеет больший* приоритет,
                // чем верхняя операции в стеке, то новая операции кладётся в стек;
                if (prior > priorPrev) {
                    stack.push(string);
                    continue;
                }
                //если новая операция имеет меньший или равный приоритет,
                if (prior <= priorPrev) {
                    //чем верхняя операции в стеке, то операции, находящиеся в стеке,
                    while (prior <= priorPrev && stack.size() != 0) {
                        //до ближайшей открывающей скобки или до операции с приоритетом меньшим,
                        result.add(stack.pop());
                        //чем у новой операции, перекладываются в формируемую запись,
                        if (stack.size() == 0) {
                            priorPrev = 0;
                        } else {
                            priorPrev = priority(stack.peek());
                        }
                    }
                    stack.push(string);                            // а новая операции кладётся в стек.
                }
            }
        }
        while (stack.size() != 0) {
            result.add(stack.pop());
        }
        list.clear();
        list.addAll(result);
    }

    public static String countPolishRecord(ArrayList<String> formula) {
        int i = 0;
        while (formula.size() != 1) {
            String first = formula.get(i);
            String second = formula.get(i + 1);
            String third = formula.get(i + 2);
            if (isNumber(third)) {
                i++;
            } else {
                int a = 0;
                try {
                    a = Integer.parseInt(first, 17);
                } catch (Exception e) {
                    System.err.println(e);
                    System.exit(1);
                }
                int b = 0;
                try {
                    b = Integer.parseInt(second, 17);
                } catch (Exception e) {
                    System.err.println(e);
                    System.exit(1);
                }
                int res = -1;
                if (third.equals("+")) {
                    if (a > 0 ? b > Integer.MAX_VALUE - a
                            : b < Integer.MIN_VALUE - a) {
                        System.err.print("Integer overflow by adding the numbers "
                                + Integer.toString(a, 17) + " and " + Integer.toString(b, 17));
                        System.exit(1);
                    }
                    res = a + b;
                } else if (third.equals("-")) {
                    if (a > 0 ? b < Integer.MIN_VALUE + a
                            : b > Integer.MAX_VALUE + a) {
                        System.err.print("Integer overflow by subtracting the numbers "
                                + Integer.toString(a, 17) + " and " + Integer.toString(b, 17));
                        System.exit(1);
                    }
                    res = a - b;
                } else if (third.equals("*")) {
                    if (a > 0 ? b > Integer.MAX_VALUE / a
                            || b < Integer.MIN_VALUE / a
                            : (a < -1 ? b > Integer.MIN_VALUE / a
                            || b < Integer.MAX_VALUE / a
                            : a == -1
                            && b == Integer.MIN_VALUE)) {
                        System.err.print("Integer overflow by multiplying the numbers "
                                + Integer.toString(a, 17) + " and " + Integer.toString(b, 17));
                        System.exit(1);
                    }
                    res = a * b;
                } else if (third.equals("/")) {
                    if (b == 0) {
                        System.err.println("Error: you can not divide by zero.");
                        System.exit(1);
                    }
                    res = a / b;
                }
                if (res == -1) {
                    System.err.println("There are the error in the function 'countPolishRecord'");
                    System.exit(1);
                }
                String resultNumber = Integer.toString(res, 17);
                resultNumber = resultNumber.toUpperCase();
                formula.remove(i + 2);
                formula.remove(i + 1);
                formula.remove(i);
                formula.add(i, resultNumber);
                i = 0;
            }
        }
        return formula.get(0);
    }

    public static void main(String[] args) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            str.append(args[i]);
            str.append(" ");
        }
        String string = str.toString().toUpperCase();
        StringTokenizer st = new StringTokenizer(string, "+/*() ", true);
        ArrayList<String> list = new ArrayList<>();
        while (st.hasMoreElements()) {
            String tmp = (String) st.nextElement();
            if (!tmp.equals(" ")) {
                String tmp2 = tmp.replaceAll(" ", "");
                list.add(tmp2);
            }
        }
        checkExpression(list);
        polishRecord(list);
        String result = countPolishRecord(list);
        System.out.println(result);
    }
}
