package ru.fizteh.fivt.students.baldindima.calc;

import java.util.Stack;
import java.io.IOException;

public class Calculater_1 {

    private static Stack<Integer> numbers = new Stack<Integer>();
    private static Stack<String> signs = new Stack<String>();

    private static String readExpression(String[] s) {
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < s.length; ++i)
            expression.append(s[i]);
        return expression.toString();
    }

    private static int signPriority(String sign) {
        if (sign.matches("[+-]")) {
            return 1;
        } else if (sign.matches("[*/]"))
            return 2;
        return 0;
    }

    private static int getNumber() throws IOException {
        if (numbers.isEmpty()) {
            throw new IOException("Your expression isn't correct");
        }
        return numbers.pop();
    }

    private static void toCount() throws IOException {

        String sign;
        if (!(signs.isEmpty())) {
            sign = signs.pop();
        } else {
            throw new IOException("Your expression isn't correct");
        }
        if (sign.equals("(")) {
            throw new IOException("Your expression isn't correct");
        }
        int r = getNumber();
        int l = getNumber();
        if (sign.equals("+")) {
            if (((l >= 0) && (Integer.MAX_VALUE - l < r)) ||
                    ((l < 0) && (Integer.MIN_VALUE - l > r))) {
                throw new ArithmeticException("Integer overflow");
            }
            numbers.push(l + r);

        } else if (sign.equals("-")) {
            if (((l >= 0) && (Integer.MAX_VALUE - l < -r)) ||
                    ((l < 0) && (Integer.MIN_VALUE - l > -r))) {
                throw new ArithmeticException("Integer overflow");
            }
            numbers.push(l - r);
        } else if (sign.equals("-")) {
            if (((l >= 0) && (Integer.MAX_VALUE - l < -r)) ||
                    ((l < 0) && (Integer.MIN_VALUE - l > -r))) {
                throw new ArithmeticException("Integer overflow");
            }
            numbers.push(l - r);
        } else if (sign.equals("*")) {
            if (((((l > 0) && (r > 0)) || ((l < 0) && (r < 0)))
                    && ((Long.MAX_VALUE / l) < r))
                    || ((((l < 0) && (r > 0)) || ((l > 0) && (r < 0)))
                    && ((Long.MIN_VALUE / l) > r))) {
                throw new ArithmeticException("Integer overflow");
            }
            numbers.push(l * r);
        } else if (sign.equals("/")) {
            if (r == 0) {
                throw new ArithmeticException("Division by zero");
            }
            numbers.push(l / r);
        }
    }

    private static int calculater(String expression) throws IOException {
        for (int i = 0; i < expression.length(); ++i) {
            String currentToken = Character.toString(expression.charAt(i));
            if (currentToken.equals("(")) {
                signs.push("(");
            } else if (currentToken.equals(")")) {
                while (!signs.peek().equals("(") && !(signs.isEmpty())) {
                    toCount();
                }
                if (!(signs.isEmpty())) {
                    signs.pop();
                } else {
                    throw new IOException("Your expression isn't correct");

                }
            } else if (currentToken.matches("[+*/-]")) {
                while (!(signs.isEmpty()) && (signPriority(signs.peek()) >= signPriority(currentToken))) {
                    toCount();
                }
                signs.push(currentToken);
            } else if (currentToken.matches("[0-9A-Ga-g]")) {
                while (((i + 1 < expression.length())) && (Character.toString(expression.charAt(i + 1)).matches("[0-9A-Ga-g]"))) {
                    currentToken += Character.toString(expression.charAt(i + 1));
                    ++i;
                }
                numbers.push(Integer.parseInt(currentToken, 17));
            } else if (!currentToken.equals(" ")) {
                throw new IOException("Invalid character");
            }

        }
        while (!signs.isEmpty())
            toCount();
        int result = numbers.pop();
        if (!numbers.isEmpty()) {
            throw new IOException("Your expression isn't corrrect");
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            String expression = readExpression(args);
            int result = calculater(expression);
            System.out.println(Integer.toString(result, 17));

        } catch (NumberFormatException exception) {
            System.err.println(exception);
        } catch (IOException exception) {
            System.err.println(exception);
        } catch (ArithmeticException exception) {
            System.err.println(exception);
        }


    }



}
