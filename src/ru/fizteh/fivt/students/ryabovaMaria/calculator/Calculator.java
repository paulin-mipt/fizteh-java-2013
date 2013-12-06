package ru.fizteh.fivt.students.ryabovaMaria.calculator;

import java.math.BigInteger;

public class Calculator {
    static String calcExpr = new String();

    static BigInteger numValue;

    static int curPosition;

    static Lexem curLex;

    enum Lexem {
        NUM,
        PLUS,
        MINUS,
        MUL,
        DIV,
        OPEN,
        CLOSE,
        END
    }

    public static void concatIntoExpr(String[] args) {
        int numOfArgs = args.length;
        StringBuilder tempString = new StringBuilder();
        for (int i = 0; i < numOfArgs; ++i) {
            tempString.append(args[i]);
            tempString.append(" ");
        }
        calcExpr = tempString.toString();
    }

    public static void getLexem() throws Exception {
        if (curPosition >= calcExpr.length()) {
            curLex = Lexem.END;
            return;
        }
        char c = calcExpr.charAt(curPosition);
        while (curPosition < calcExpr.length()
                && Character.isWhitespace(c)) {
            ++curPosition;
            if (curPosition >= calcExpr.length()) {
                break;
            }
            c = calcExpr.charAt(curPosition);
        }
        if (curPosition >= calcExpr.length()) {
            curLex = Lexem.END;
            return;
        }
        if (Character.isDigit(c) || Character.isLetter(c)) {
            curLex = Lexem.NUM;
        } else {
            switch (c) {
                case '+':
                    curLex = Lexem.PLUS;
                    break;
                case '-':
                    curLex = Lexem.MINUS;
                    break;
                case '*':
                    curLex = Lexem.MUL;
                    break;
                case '/':
                    curLex = Lexem.DIV;
                    break;
                case '(':
                    curLex = Lexem.OPEN;
                    break;
                case ')':
                    curLex = Lexem.CLOSE;
                    break;
                default:
                    throw new Exception("Bad symbol");
            }
            ++curPosition;
            return;
        }
        StringBuilder tempString = new StringBuilder();
        while (curPosition < calcExpr.length()) {
            c = calcExpr.charAt(curPosition);
            if (Character.isLetterOrDigit(c)) {
                tempString.append(c);
            } else {
                break;
            }
            ++curPosition;
        }
        numValue = new BigInteger(tempString.toString(), 19);
    }

    public static BigInteger parseMultiplier() throws Exception {
        switch (curLex) {
            case NUM:
                BigInteger answer = numValue;
                getLexem();
                return answer;
            case OPEN:
                getLexem();
                answer = parseExpr();
                if (curLex == Lexem.CLOSE) {
                    getLexem();
                } else {
                    throw new Exception("Bad bracket balance");
                }
                return answer;
            case CLOSE:
                throw new Exception("Early close bracket");
            case END:
                throw new Exception("Early end");
            case MUL:
            case DIV:
            case PLUS:
            case MINUS:
                throw new Exception("Extra sign");
            default:
        }
        return BigInteger.ZERO;
    }

    public static BigInteger parseSummand() throws Exception {
        BigInteger ans = parseMultiplier();
        while (curLex == Lexem.MUL || curLex == Lexem.DIV) {
            Lexem curSign = curLex;
            getLexem();
            if (curSign == Lexem.MUL) {
                ans = ans.multiply(parseMultiplier());
            }
            if (curSign == Lexem.DIV) {
                BigInteger nextValue = parseMultiplier();
                if (nextValue == BigInteger.ZERO) {
                    throw new Exception("Division by zero");
                }
                ans = ans.divide(nextValue);
            }
        }
        return ans;
    }

    public static BigInteger parseExpr() throws Exception {
        BigInteger ans = parseSummand();
        while (curLex == Lexem.PLUS || curLex == Lexem.MINUS) {
            Lexem curSign = curLex;
            getLexem();
            if (curSign == Lexem.PLUS) {
                ans = ans.add(parseSummand());
            }
            if (curSign == Lexem.MINUS) {
                ans = ans.subtract(parseSummand());
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        concatIntoExpr(args);
        curPosition = 0;
        try {
            getLexem();
            if (curLex == Lexem.END) {
                System.err.println("Please, write arithmetic expression");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        try {
            BigInteger answer = parseExpr();
            if (curLex != Lexem.END) {
                if (curLex == Lexem.CLOSE) {
                    System.err.println("Bad balance");
                } else {
                    System.err.println("Bad lexem");
                }
                System.exit(1);
            } else {
                System.out.println(answer.toString(19));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
