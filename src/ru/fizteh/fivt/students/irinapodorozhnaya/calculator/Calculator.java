package ru.fizteh.fivt.students.irinapodorozhnaya.calculator;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class Calculator {
    private static final int RADIX = 18;
    private static final int FIGURE = 4;
    private static final int OPERATOR = 1;
    private static final int LEFT_BRACKET = 2;
    private static final int RIGHT_BRACKET = 3;
    
    public static void main(String[] argv) {
        String res = "";
        if (argv.length == 0) {
            System.out.println("Usage: <program name> <calculating expression>");
            System.exit(1);
        }
        try {
            if (!isCorrect(argv)) {
                System.err.println("Incorrect syntax");
                System.exit(2);
            }
            String postfixForm = dijkstraSortStation(argv);
            res = Integer.toString(calc(postfixForm), RADIX);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (NumberFormatException f) {
            System.err.println(f.getMessage());
            System.err.println("Can't convert to Integer, probably value is too big");
            System.exit(3);
        } catch (ArithmeticException e) {
            System.err.println(e.getMessage());
            System.exit(4);
        }
        System.out.println(res);
        System.exit(0);
    }
    
    private static int operatorPriority(char operator) {
        if (operator == '*' || operator == '/') {
            return 2;
        } else {
            return 1;
        }
    }
    
    private static boolean isCorrect(String[] argv) throws IOException {
        int prev = 0;
        boolean wasSpace = true;
        for (String s: argv) {
            wasSpace = true;
            for (int i = 0; i < s.length(); ++i) {
                char token = s.charAt(i);
                if (isFigure(token)) {
                    if ((prev == FIGURE && wasSpace) || prev == RIGHT_BRACKET) {
                        return false;
                    }
                    wasSpace = false;
                    prev = FIGURE;
                } else if (isOperator(token)) {
                    if (prev == OPERATOR || prev == LEFT_BRACKET) {
                        return false;
                    }
                    prev = OPERATOR;
                } else if (token == '(') {
                    if (prev == FIGURE || prev == RIGHT_BRACKET) { 
                        return false;
                    }
                    prev = LEFT_BRACKET;
                } else if (token == ')') {
                    if (prev == OPERATOR || prev == LEFT_BRACKET) {
                        return false;
                    }
                    prev = RIGHT_BRACKET;
                } else if (token == ' ') {
                    wasSpace = true;
                } else {
                    throw new IOException("Unrecognized character");
                }
            }
        }
        return true;
    }
    
    private static String dijkstraSortStation(String[] argv) throws IOException {
        Deque<Character> operatorsStack = new ArrayDeque<Character>();
        StringBuilder out = new StringBuilder();
        for (String s: argv) {
            for  (int i = 0; i < s.length(); ++i) {
                char token = s.charAt(i);        
                if (isFigure(token)) {
                    out.append(token);
                } else if (isOperator(token)) {
                    out.append(' ');
                    while (!operatorsStack.isEmpty() && operatorsStack.getFirst() != '(') {
                        if (operatorPriority(token) 
                                <= operatorPriority(operatorsStack.getFirst())) {
                            out.append(operatorsStack.pop());    
                        } else { 
                            break;
                        }
                    }
                    operatorsStack.push(token);
                } else if (token == '(') {
                    operatorsStack.push(token);
                } else if (token == ')') {
                    while (operatorsStack.getFirst() != '(') {
                        if (!operatorsStack.isEmpty()) {
                            out.append(' ');
                            out.append(operatorsStack.pop());
                        } else { 
                            throw new IOException("Bracket mismatched");
                        }
                    }
                    operatorsStack.pop();
                }
            }
        }    
        while (!operatorsStack.isEmpty()) {
            if (operatorsStack.getFirst() != '(') {
                out.append(' ');
                out.append(operatorsStack.pop());
            } else { 
                throw new IOException("Bracket mismatched");
            }
        }
        return out.toString();
    } 
    
    private static int calc(String postfixForm) throws IOException {
        Deque<Integer> numbers = new ArrayDeque<>();
        for (int i = 0; i < postfixForm.length(); ++i) {
            char token = postfixForm.charAt(i);
            
            if (isFigure(token)) {
                int t = i;
                while (i < postfixForm.length() && postfixForm.charAt(i) != ' ') {
                    ++i;
                }
                String numberStr = postfixForm.substring(t, i);
                numbers.push(Integer.parseInt(numberStr, RADIX));
            } else if (isOperator(token)) {
                if (numbers.size() < 2) {
                    throw new IOException("Incorrect syntax");
                } 
                int op1 = numbers.pop();
                int op2 = numbers.pop();
                
                if (token == '+') {
                    if (Integer.MAX_VALUE - op1 < op2) {
                        throw new IOException("Too big value");
                    }
                    numbers.push(op1 + op2);
                } else if (token == '*') {
                    if (op1 != 0 && Integer.MAX_VALUE / op1 < op2) {
                        throw new IOException("Too big value");
                    }
                    numbers.push(op1 * op2);
                } else if (token == '-') {
                    if (Integer.MIN_VALUE + op1 > op2) {
                        throw new IOException("Too small value");
                    }
                    numbers.push(op2 - op1);
                } else if (token == '/') {
                    numbers.push(op2 / op1);
                }
            }
        }
        return numbers.pop();
    }
    
    private static boolean isFigure(char token) {
        return (token >= '0' && token <= '9') || (token >= 'A' && token <= 'A' + RADIX)
                || (token >= 'a' && token <= 'a' + RADIX);
    }

    private static boolean isOperator(char token) {
        return (token == '+' || token == '-' || token == '*' || token == '/');
    }
}
