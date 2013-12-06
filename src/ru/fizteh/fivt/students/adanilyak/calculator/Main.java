package ru.fizteh.fivt.students.adanilyak.calculator;

/**
 * User: Alexander
 * Date: 29.09.13
 * Time: 15:00
 */

public class Main {

    public static void main(String[] args) {
        StringBuilder str = new StringBuilder("");
        for (String temp : args) {
            str.append(temp);
            str.append(" ");
        }
        Integer radix = 18;
        Calculator calculator = new Calculator(str.toString());
        // Вывод в той же системе счисления, в которой производятся действия
        System.out.println(calculator.calculate(radix));
    }
}
