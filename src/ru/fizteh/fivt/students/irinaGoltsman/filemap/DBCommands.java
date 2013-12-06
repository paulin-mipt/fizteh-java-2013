package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import ru.fizteh.fivt.students.irinaGoltsman.shell.*;

public class DBCommands {
    public static class Use implements Command {
        protected final String name = "use";
        protected final int countOfArguments = 1;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.use(args);
        }
    }

    public static class Commit implements Command {
        protected final String name = "commit";
        protected final int countOfArguments = 0;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.commit();
        }
    }

    public static class CreateTable implements Command {
        protected final String name = "create";
        //Название таблицы и список типов колонок
        protected final int countOfArguments = 2;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.createTable(args);
        }
    }

    public static class Drop implements Command {
        protected final String name = "drop";
        protected final int countOfArguments = 1;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.removeTable(args);
        }
    }

    public static class Put implements Command {
        protected final String name = "put";
        protected final int countOfArguments = 2;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.put(args);
        }
    }

    public static class Get implements Command {
        protected final String name = "get";
        protected final int countOfArguments = 1;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.get(args);
        }
    }

    public static class Remove implements Command {
        protected final String name = "remove";
        protected final int countOfArguments = 1;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.remove(args);
        }
    }

    public static class Size implements Command {
        protected final String name = "size";
        protected final int countOfArguments = 0;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.size();
        }
    }

    public static class RollBack implements Command {
        protected final String name = "rollback";
        protected final int countOfArguments = 0;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getCountOfArguments() {
            return countOfArguments;
        }

        @Override
        public boolean check(String[] parts) {
            return ((parts.length - 1) == countOfArguments);
        }

        @Override
        public Code perform(String[] args) {
            return DataBase.rollBack();
        }
    }
}
