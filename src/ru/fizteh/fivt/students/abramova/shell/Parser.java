package ru.fizteh.fivt.students.abramova.shell;

public class Parser {
    public static String[][] parseArgs(String[] args) {
        //Собираем все аргументы вместе
        StringBuilder longArgumentBuilder = new StringBuilder();
        for (String str : args) {
            longArgumentBuilder.append(str).append(" ");
        }
        String longArgument = longArgumentBuilder.toString();
        //Разбиваем на команды с аргументами
        String[] commandsWithArgs = longArgument.split(";");
        String[][] returnValue = new String[commandsWithArgs.length + 1][];
        returnValue[0] = new String[commandsWithArgs.length];
        //Отделяем команды и аргументы
        int commandNumber = 0;
        int shift;   //Сдвиг на случай, если первый символ пробел
        for (String cmd : commandsWithArgs) {
            shift = 0;
            String[] separated = cmd.split(" ");
            //Все команды записываются в returnValue[0][commandNumber]
            if (cmd.charAt(0) == ' ') {
                shift = 1;
            }
            returnValue[0][commandNumber] = separated[shift];
            //А соотвествтвующие аргументы в returnValues[commandNumber + 1]
            if (separated.length - shift != 1) {
                returnValue[commandNumber + 1] = new String[separated.length - 1 - shift];
                for (int i = 1; i < separated.length - shift; i++) {
                   returnValue[commandNumber + 1][i - 1] = separated[i + shift];
                }
            }
            commandNumber++;
        }
        return returnValue;
    }
}
