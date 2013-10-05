package ru.fizteh.fivt.students.MazanovArtem.calculator;

import java.math.BigInteger;
import java.util.Stack;

public class Calculator {
	 final static int RADIX = 19;
     public static void main(String[] args) {
    	 StringBuilder Inputdata = new StringBuilder();
    	 Inputdata.append('(');
    	 if (args.length == 0) {
    		 System.out.println("Hello!!!");
    		 System.exit(0);
    	 } else {
    		 for (int i = 0;i < args.length;++i) {
    			 if ((i > 0) && (IsDigit(args[i - 1].charAt(args[i - 1].length() - 1)))
    			     && (IsDigit(args[i].charAt(0)))) {
    				 System.out.println("Wrong input data");
    				 System.exit(1);
    			 }
    			 Inputdata.append(args[i]);
    		 }
    		 Inputdata.append(')');
    	 }
    	 try {
    		 IsRightData(Inputdata.toString());
    	 } catch (Exception exp) {
    		 System.err.println(exp.getLocalizedMessage());
    		 System.exit(1);
    	 }
    	 Inputdata = DeleteSpaces(Inputdata);
    	 System.out.println(Inputdata);
    	 System.out.println(Calc(Inputdata));
     }
     
     private static boolean IsDigit(char k) {
    	 if ((k >= '0') && (k <= '9') || (k >= 'a') && (k <= RADIX - 10 + 'a')) {
    		 return true;
    	 } else {
    		 return false;
    	 }
     }
     
     private static void IsRightData(String Input) throws Exception {
    	 int thischar = 6;
    	 //0 = действие,
    	 //1 = открывающая скобка
    	 //2 = закрывающая скобка
    	 //3 = циферка
    	 //4 = пробел
    	 //5 = что-то левое
    	 //6 = нейтральный(начальный)
    	 int nextchar = 6;//аналогично
    	 int lastchar = 6;
    	 int bracketsum = 0;
    	 for (int i = 0;i < Input.length();++i) {
    		 thischar = ThatChar(Input.charAt(i));
    		 switch (thischar) {
    		 case 0:
    			 if (lastchar == 0) {
    				 throw new Exception("Лишний оператор");
    			 }
    			 if ((lastchar == 1) && (Input.charAt(i) != '-')) {
    				 throw new Exception("Неуместный оператор"); 
    			 }
    			 lastchar = thischar;
    			 break;
    		 case 1:
    			 if ((lastchar == 2) || (lastchar == 3)) {
    				 throw new Exception("Не хватает оператора");
    			 }
    			 bracketsum++;
    			 lastchar = thischar;
    			 break;
    		 case 2:
    			 if (lastchar == 0) {
    				 throw new Exception("Не хватает числа");
    			 }
    			 if (lastchar == 1) {
    				 throw new Exception("Что вы хотели написать в пустых скобках?");
    			 }
    			 bracketsum--;
    			 if (bracketsum < 0) {
    				 throw new Exception("Неверная скобочная последовательность");
    			 }
    			 lastchar = thischar;
    			 break;
    		 case 3:
    			 if (lastchar == 2) {
    				 throw new Exception("Не хватает оператора");
    			 }
    			 lastchar = thischar;
    			 break;
    		 case 4:
    			 nextchar = ThatChar(Input.charAt(i + 1));//за конец строки не вылезем,так как в конце всегда стоит ')'
    			 if ((lastchar == 3) && (nextchar == 3)) {
    				 throw new Exception("Не хватает оператора");
    			 }
    			 break;
    		 case 5:
    			 throw new Exception("Неизвестные символы");
    		 }
    	 }
    	 if (bracketsum != 0) {
    		 throw new Exception("Неверная скобочная последовательность");
    	 }
     }
     
     private static StringBuilder DeleteSpaces(StringBuilder Input) {
    	 int i = 0;
    	 while (i < Input.length()) {
    		 if (Input.charAt(i) == ' ') {
    			 Input.deleteCharAt(i);
    		 } else {
    			 ++i;
    		 }
    	 }
    	 return Input;
     }
     
     private static int ThatChar(char k) {
    	 if (k == '(') {
    		 return 1;
    	 }
    	 if (k == ')') {
    		 return 2;
    	 }
    	 if (IsDigit(k)) {
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
     
     private static String Calc(StringBuilder Inputdata) {
    	 Stack<String> stack = new Stack<String>();
    	 Stack<String> stack1 = new Stack<String>();
    	 StringBuilder Temp1 = new StringBuilder();
    	 StringBuilder Temp2 = new StringBuilder();
    	 StringBuilder Temp3 = new StringBuilder();
    	 StringBuilder Temp = new StringBuilder();
    	 BigInteger a;
    	 BigInteger b;
    	 int temp = 0;//для хранения позиции,на которой была последняя открывающая скобка-чтобы уметь считывать отрицательные числа
    	 for (int i = 0;i < Inputdata.length();++i) {
    		 if (Inputdata.charAt(i) == '(') {
    			 stack.push("(");
    			 temp = i;
    		 }
    		 if (IsDigit(Inputdata.charAt(i))) {
    			 Temp.append(Inputdata.charAt(i));
    		 }
    		 if (ThatChar(Inputdata.charAt(i)) == 0) {//проверка на принадлежность оператору
    			 if ((Inputdata.charAt(i) == '-') && (temp == i - 1)) {
    				 Temp.append('-');
    				 continue;
    			 } else {
    				 stack.push(Temp.toString());
        			 Temp.delete(0, Temp.length() - 1);
    			 }
				 if ((Inputdata.charAt(i) == '+') || (Inputdata.charAt(i) == '-')) {
					 Temp1 = new StringBuilder(stack.pop());
					 Temp2 = new StringBuilder(stack.pop());
					 if (Temp2.charAt(0) != '(') {
						 Temp3 = new StringBuilder(stack.pop());
						 a = new BigInteger(Temp1.toString(), RADIX);
	    				 b = new BigInteger(Temp3.toString(), RADIX);
						 switch (Temp2.charAt(0)) {
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
							 a = b.divide(a);
							 stack.push(a.toString(RADIX));
							 break;
						 }
					 } else {
						 stack.push(Temp2.toString());
						 stack.push(Temp1.toString());
					 }
					 if (Inputdata.charAt(i) == '+') {
						 stack.push("+");
					 } else {
						 stack.push("-");
					 }
				 } else {
					 Temp1 = new StringBuilder(stack.pop());
					 Temp2 = new StringBuilder(stack.pop());
					 if ((Temp2.charAt(0) != '(') && (Temp2.charAt(0) != '-') && (Temp2.charAt(0) != '+')) {
						 Temp3 = new StringBuilder(stack.pop());
						 a = new BigInteger(Temp1.toString(), RADIX);
	    				 b = new BigInteger(Temp3.toString(), RADIX);
						 switch (Temp2.charAt(0)) {
						 case '*':
							 a = a.multiply(b);
							 stack.push(a.toString(RADIX));
							 break;
						 case '/':
							 a = b.divide(a);
							 stack.push(a.toString(RADIX));
							 break;
						 }
					 } else {
						 stack.push(Temp2.toString());
						 stack.push(Temp1.toString());
					 }
					 if (Inputdata.charAt(i) == '*') {
						 stack.push("*");
					 } else {
						 stack.push("/");
					 }
				 }
    			 Temp.delete(0, Temp.length());
    		 }
    		 if (Inputdata.charAt(i) == ')') {
    			 if (Temp.length() > 0) {
    				 stack.push(Temp.toString());
    				 Temp.delete(0,Temp.length());
    			 }
    			 Temp1 = new StringBuilder(stack.pop());
    			 Temp2 = new StringBuilder(stack.pop());
    			 if ((Temp2.charAt(0) == '*') || (Temp2.charAt(0) == '/')) {
    				 Temp3 = new StringBuilder(stack.pop());
    				 a = new BigInteger(Temp1.toString(), RADIX);
    				 b = new BigInteger(Temp3.toString(), RADIX);
    				 if (Temp2.charAt(0) == '*') {
    					 a = a.multiply(b);
    					 stack.push(a.toString(RADIX));
    				 } else {
    					 a = b.divide(a);
    					 stack.push(a.toString(RADIX));
    				 }
    			 } else {
    				 stack.push(Temp2.toString());
    				 stack.push(Temp1.toString());
    			 }
    			 Temp1 = new StringBuilder(stack.pop());
    			 while (Temp1.charAt(0) != '(') {
    				 stack1.push(Temp1.toString());
    				 Temp1 = new StringBuilder(stack.pop());
    			 }
    			 while (stack1.size() > 1) {
    				 Temp1 = new StringBuilder(stack1.pop());
    				 Temp2 = new StringBuilder(stack1.pop());
    				 Temp3 = new StringBuilder(stack1.pop());
    				 a = new BigInteger(Temp1.toString(), RADIX);
    				 b = new BigInteger(Temp3.toString(), RADIX);
    				 if (Temp2.charAt(0) == '+') {
    					 a = a.add(b);
    					 stack1.push(a.toString(RADIX));
    				 } else {
    					 a = a.subtract(b);
    					 stack1.push(a.toString(RADIX));
    				 }
    			 }
    			 stack.push(stack1.pop());
    		 }
    	 }
    	 return stack.pop();
     }
}
