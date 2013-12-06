package ru.fizteh.fivt.students.eltyshev.calc;

import java.io.IOException;

public class Program
{
    public static void main(String[] Args)
    {
        if (Args.length == 0)
        {
            System.out.println("Usage: calc \"1+2\"");
            System.exit(-1);
        }
        StringBuilder sb = new StringBuilder();
        for(String arg: Args)
        {
            sb.append(arg + " ");
        }
        try
        {
            String result = ExpressionSolver.execute(sb.toString());
            System.out.print(result);
        }
        catch(IOException e)
        {
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }
        catch (ArithmeticException e)
        {
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }
    }
}
