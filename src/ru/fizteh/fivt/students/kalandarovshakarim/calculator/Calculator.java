package ru.fizteh.fivt.students.kalandarovshakarim.calculator;

import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

public class Calculator {
    public static int prior(String sign) {
        if (sign.equals("*") || sign.equals("/")) {
            return 3;
        }
        if (sign.equals("+") || sign.equals("-")) {
            return 2;
        }
        if (sign.equals("(")) {
            return 1;
        }
        return 0;
    }
    
    public static void checkOverflow(long res, Stack<Integer> solution) {
        if (res >= Integer.MIN_VALUE && res <= Integer.MAX_VALUE) {
            solution.push((int) res);
        } else {
            System.out.println("Int overflow");
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        StringBuilder expBuilder = new StringBuilder();
        String signsPattern = "+-*/()";
       
        if (args.length == 0) {
            System.out.print("Usage: java Calculator [Math expression,\n");
            System.out.println("which contains 19 based digits and \"+-*/()\"]");
            System.exit(1);
        }
       
        for (int i = 0; i < args.length; ++i) {
            for (int j = 0; j < args[i].length(); ++j) {
                char letter = args[i].charAt(j);
                if ((letter >= '0' && letter <= '9') 
                        || (letter >= 'a' && letter <= 'i')
                        || (letter >= 'A' && letter <= 'I')) {
                    expBuilder.append(letter);
                } else if (signsPattern.indexOf(letter) != -1) {
                    expBuilder.append(' ');
                    expBuilder.append(letter);
                    expBuilder.append(' ');
                } else if (letter != ' ') {
                    System.out.print("expression contains unrecognized characters in ");
                    System.out.println(args[i]);
                    System.exit(1);
                }
            }
            expBuilder.append(' ');
        }

        String exp = expBuilder.toString().toLowerCase();
        Stack<String> lifo = new Stack<String>();
        Vector<String> list = new Vector<String>();
        StringBuilder operandBuilder = new StringBuilder();
        String operand;
        
        for (int i = 0; i < exp.length(); ++i) {
            if (exp.charAt(i) == ' ') {
              
                operand = operandBuilder.toString();

                if (signsPattern.indexOf(operand) != -1 && operand.length() > 0) {
                    if (operand.equals(")")) {
                        String popedSign;
                        do {
                            if (lifo.empty()) {
                                System.out.println("Error in brackets");
                                System.exit(1);
                            }
                            popedSign = lifo.pop();
                            if (!popedSign.equals("(")) {
                                list.add(popedSign);
                            }
                        } while (!popedSign.equals("("));
                    }
                 
                    if (operand.equals("(")) {
                        lifo.push(operand);
                    }
                 
                    if (operand.equals("+") || operand.equals("-") 
                            || operand.equals("*") || operand.equals("/")) {
                        if (lifo.empty()) {
                            lifo.push(operand);
                        } else if (prior(lifo.peek()) < prior(operand)) {
                            lifo.push(operand);                           
                        } else {
                            while (!lifo.empty() && prior(lifo.peek()) >= prior(operand)) {
                                String Pop = lifo.pop();
                                list.add(Pop);
                            }
                            lifo.push(operand);                         
                        }
                    }
                 
                } else if (operand.length() > 0) {
                    list.add(operand);
                }

                operandBuilder.delete(0, operand.length());

                continue;
            }
          
            operandBuilder.append(exp.charAt(i));
        }
       
       
        while (!lifo.empty()) {
            list.add(lifo.pop());
        }
              
        Stack<Integer> solution = new Stack<Integer>();
        
        for (int i = 0; i < list.size(); ++i) {
            String obj = list.elementAt(i);
           
            long left = 0;
            long right = 0;
            long res = 0;

            if (prior(obj) > 0) {
                if (solution.size() < 2) {
                    System.out.println("Your expression is wrong");
                    System.exit(1);
                }
                right = solution.pop();
                left = solution.pop();
            }
           
            if (obj.equals("+")) {
                res = left + right;
                checkOverflow(res, solution);
            } else if (obj.equals("-")) {
                res = left - right;
                checkOverflow(res, solution);
            } else if (obj.equals("*")) {
                res = left * right;
                checkOverflow(res, solution);
            } else if (obj.equals("/")) {
                if (right == 0) {
                    System.out.println("Division by zero");
                    System.exit(1);
                }
                res = left / right;
                checkOverflow(res, solution);
            } else {
                try {
                    solution.push(Integer.parseInt(obj, 19));
                } catch (Exception e) {
                    System.out.println("Your entered wrong expression");
                    System.exit(1);
                }
              
            }
        }
       
        if (solution.empty()) {
            System.out.println("Your entered wrong expression");
            System.exit(1);
        }
       
        int result = solution.pop();      
       
        if (!solution.empty()) {
            System.out.println("Your entered wrong expression");
            System.exit(1);
        }
       
        System.out.println(Integer.toString(result, 19));
       
        System.exit(0);
    }
}
