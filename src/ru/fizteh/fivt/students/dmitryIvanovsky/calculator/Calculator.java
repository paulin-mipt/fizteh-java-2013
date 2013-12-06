package ru.fizteh.fivt.students.dmitryIvanovsky.calculator;

import java.math.BigInteger;

public class Calculator {

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg.toUpperCase());
            builder.append(' ');
        }
        String query = builder.toString();
        if (query.equals("")) {
            System.err.println("Пустой ввод");
            System.exit(1);
        }
        try {
            MyCalc calculator = new MyCalc(query);
            String res = calculator.result();
            System.out.println(res);
        } catch (ErrorFormula e) {
            System.err.println(e);
            System.exit(1);
        }

    }

}

class MyCalc {

    private enum Lex {FIRST, NUM, PLUS, MUL, OPEN, CLOSE, END, MINUS, DEL}
    private Lex curlex;
    private int it;
    private BigInteger value;
    private int variant;
    private String formula;
    private String resFormula;

    public MyCalc(String s) {
        formula = s;
        curlex = Lex.FIRST;
        it = 0;
        value = BigInteger.ZERO;
        variant = 17;
    }

    public boolean isSuitableSymbol(char c) {
        return ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'G'));
    }

    public int convertToInt(char c) {
        if (c >= 'A' && c <= 'G') {
            return c - 'A' + 10;
        } else {
            return c - '0';
        }
    }

    private void nextLexem() throws ErrorFormula {
        while (it < formula.length() && Character.isWhitespace(formula.charAt(it))) {
            it += 1;
        }

        if (it >= formula.length()) {
            curlex = Lex.END;
            return;
        }
        char c = formula.charAt(it);

        switch (c) {
            case '(':
                curlex = Lex.OPEN;
                it += 1;
                break;
            case ')':
                curlex = Lex.CLOSE;
                it += 1;
                break;
            case '+':
                curlex = Lex.PLUS;
                it += 1;
                break;
            case '-':
                curlex = Lex.MINUS;
                it += 1;
                break;
            case '*':
                curlex = Lex.MUL;
                it += 1;
                break;
            case '/':
                curlex = Lex.DEL;
                it += 1;
                break;
            default:
                if (isSuitableSymbol(c)) {
                    int digit;
                    curlex = Lex.NUM;
                    value = BigInteger.ZERO;
                    while (it < formula.length() && isSuitableSymbol(formula.charAt(it))) {
                        digit = convertToInt(formula.charAt((it)));
                        value = value.multiply(BigInteger.valueOf(variant));
                        value = value.add(BigInteger.valueOf(digit));
                        it += 1;
                    }
                } else {
                    String error = String.format("Неизвестный символ '%c' в \"%s\"= , номер %d", c, formula, it);
                    throw new ErrorFormula(error);
                }
        }
    }

    private BigInteger expression() throws ErrorFormula {
        BigInteger first = item();
        while (curlex == Lex.PLUS || curlex == Lex.MINUS) {
            Lex tmp = curlex;
            nextLexem();
            BigInteger second = item();
            if (tmp == Lex.PLUS) {
                first = first.add(second);
            } else {
                first = first.subtract(second);
            }
        }
        return first;
    }

    private BigInteger item() throws ErrorFormula {
        BigInteger first = multiplier();
        while (curlex == Lex.MUL || curlex == Lex.DEL) {
            Lex tmp = curlex;
            nextLexem();
            BigInteger second = multiplier();
            if (tmp == Lex.MUL) {
                first = first.multiply(second);
            } else {
                if (second.equals(BigInteger.ZERO)) {
                    throw new ErrorFormula(String.format("Деление на ноль в \"%s\", номер %d ", formula, it));
                }
                first = first.divide(second);

            }
        }
        return first;
    }

    private BigInteger multiplier() throws ErrorFormula {
        BigInteger factorRes;
        switch (curlex) {
            case NUM:
                factorRes = value;
                nextLexem();
                break;
            case OPEN:
                nextLexem();
                factorRes = expression();
                if (curlex == Lex.CLOSE) {
                    nextLexem();
                } else {
                    String error = String.format("Нарушен баланс скобок в \"%s\", номер %d", formula, it);
                    throw new ErrorFormula(error);
                }
                break;
            default:
                char c;
                if (it < formula.length()) {
                    c = formula.charAt(it);
                } else {
                    c = formula.charAt(it - 1);
                }
                String error = String.format("Неверное выражение \"%s\", номер %d, символ \'%c\'", formula, it, c);
                throw new ErrorFormula(error);
        }
        return factorRes;
    }

    public String castNumeralSystem(BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return "0";
        }
        int sign = 1;
        if (n.compareTo(BigInteger.ZERO) < 0) {
            sign = -1;
            n = n.negate();
        }

        String res = "";
        while (!n.equals(BigInteger.ZERO)) {
            int modulo = n.remainder(BigInteger.valueOf(variant)).intValue();
            if (modulo <= 9) {
                res += (char) ('0' + modulo);
            } else {
                res += (char) ('A' + modulo - 10);
            }
            n = n.divide(BigInteger.valueOf(variant));
        }
        String reverse = new StringBuilder(res).reverse().toString();
        if (sign < 0) {
            return '-' + reverse;
        } else {
            return  reverse;
        }
    }

    public String result() throws ErrorFormula {
        if (curlex == Lex.END) {
            return resFormula;
        }
        nextLexem();
        String res = castNumeralSystem(expression());
        if (curlex != Lex.END) {
            char c;
            if (it < formula.length()) {
                c = formula.charAt(it);
            } else {
                c = formula.charAt(it - 1);
            }
            String error = String.format("Неверное выражение \"%s\", номер %d, символ \'%c\'", formula, it, c);
            throw new ErrorFormula(error);
        }
        resFormula = res;
        return resFormula;
    }
}

class ErrorFormula extends Exception {
    public ErrorFormula(String text) {
        super(text);
    }
}
