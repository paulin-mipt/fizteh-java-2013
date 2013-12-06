package ru.fizteh.fivt.students.elenav.calc;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Calculator {
    
    public static int priority(char operator) {
        switch(operator) {
            case '(': return 0;
            case ')': return 1;
            case '+': return 2;
            case '-': return 2;
            case '*': return 3;
            case '/': return 3;
            case ' ': return -2;
            default: return -1;
        }
    }
    
    public static String toPostfix(String inputString) throws IOException {
        StringBuilder sb = new StringBuilder();
        Deque<Character> operators = new LinkedList<>();
        int i = 0;
        int lastNumberFlag = 0;
        int spaceFlag = 0;
        while (i < inputString.length()) {
            char token = inputString.charAt(i);
            if ((token >= '0' && token <= '9') || (token >= 'A' && token <= 'G')) {
                sb.append(token);
                ++i;
                if (lastNumberFlag == 1 && spaceFlag == 1) {
                    throw new IOException("Invalid input");
                } else {
                    spaceFlag = 0;
                }
                lastNumberFlag = 1;
                continue;
            }
            if (operators.isEmpty() && priority(token) >= 0) {
               sb.append(' ');
               operators.push(token);
               ++i;
               lastNumberFlag = 0;
               spaceFlag = 0;
               continue;
            }
            if (token == '(') {
                operators.push(token);
                ++i;
                lastNumberFlag = 0;
                spaceFlag = 0;
                continue;
            }
            if (token == ')') {
                sb.append(' ');
                lastNumberFlag = 1;
                while (operators.getFirst() != '(') {
                    sb.append(operators.getFirst());
                    operators.pop();
                }
                operators.pop();
                ++i;
                continue;
            }
            if (priority(token) >= 0) {
                sb.append(' ');
                lastNumberFlag = 0;
                while (!operators.isEmpty() && (priority(operators.getFirst()) >= priority(token))) {
                    sb.append(operators.getFirst());
                    operators.pop();
                }
                operators.push(token);
                spaceFlag = 0;
                ++i;
                continue;
            } 
            if (priority(token) == -2) {
                spaceFlag = 1;
                ++i;
            } else {
                throw new IOException("You've entered a strange symbol '" + Character.toString(token) 
                                                         + "'. I would not work with him. Try again");
            }
        }
        sb.append(' ');
        while (!operators.isEmpty()) {
            if (operators.getFirst() == '(' || operators.getFirst() == ')') {
                throw new IOException("invalid input");
            }
            sb.append(operators.pop());
        }   
        return sb.toString();
    }
    
    public static int getNumber(String convertString, int i) {
        int length = convertString.length() - 1;
        StringBuilder sb = new StringBuilder();
        while (i < length && convertString.charAt(i) != ' ') {
            sb.append(convertString.charAt(i));
            ++i;
        }
        return Integer.parseInt(sb.toString(), 17);
    }
    
    public static int calculate(String convertString) throws IOException {
        LinkedList<Integer> numbers = new LinkedList<>();
        for (int i = 0; i < convertString.length(); ++i) {
            char token = convertString.charAt(i);
            if ((token >= '0' && token <= '9') || (token >= 'A' && token <= 'G')) {
                try {
                    numbers.push(getNumber(convertString, i));
                } catch (NumberFormatException e) {
                    throw new IOException("Int overflow");
                }
                while (convertString.charAt(i) != ' ') {
                    ++i;
                }
                continue;
            }
            if (numbers.size() < 2 && priority(token) >= 0) {
                throw new IOException("Invalid input");
            }
            if (token == '+') {
                int x1 = numbers.pop();
                int x2 = numbers.pop();
                if (Integer.MAX_VALUE - Math.abs(x1) < Math.abs(x2)) {
                    throw new IOException("Int overflow");
                }
                numbers.push(x1 + x2);
                continue;
            }
            if (token == '/') {
                int temp = numbers.pop();
                if (temp == 0) {
                    throw new IOException("Division by zero");
                }
                temp =  numbers.pop() / temp;
                numbers.push(temp);
                continue;
            }
            if (token == '-') {
                int x1 = numbers.pop();
                int x2 = numbers.pop();
                if (Integer.MIN_VALUE + x1 > x2) {
                    throw new IOException("Int overflow");
                }
                numbers.push(x2 - x1);
                continue;
            }
            if (token == '*') {
                int x1 = numbers.pop();
                int x2 = numbers.pop();
                if (x1 != 0) {
                    if (Integer.MAX_VALUE / Math.abs(x1) < Math.abs(x2)) {
                        throw new IOException("Int overflow");
                    }
                }
                numbers.push(x1 * x2);
                continue;
            }
        }
        return numbers.getLast();
    }

    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (args.length == 0) {
            System.err.println("No args given. Try 2 + 3 ");
            System.exit(1);
        }
        for (String s : args) {
            sb.append(s);
            sb.append(" ");
        }
        String expression = sb.toString();
        int result = 0;
        try {
            String convertString = toPostfix(expression);
            try {
                result = calculate(convertString);
            } catch (NoSuchElementException e) {
                System.err.println("Invalid input"); 
                System.exit(1);
            }
            String outStr = Integer.toString(result, 17);
            System.out.println(outStr);
        } catch (IOException err) {
            System.err.println(err); 
            System.exit(1);
        }
        System.exit(0);
    }
}
