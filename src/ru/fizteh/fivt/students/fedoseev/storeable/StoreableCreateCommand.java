package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StoreableCreateCommand extends AbstractCommand<StoreableState> {
    public StoreableCreateCommand() {
        super("create", -1);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException, ClassNotFoundException {
        String tableName = input[0];
        File curTableDir = new File(state.getCurDir(), tableName);

        if (!tableName.matches("[^><\"?|*.]*")) {
            throw new RuntimeException("CREATE ERROR: illegal symbol in table name");
        }

        input = checkInput(input, curTableDir);

        String[] columnTypes = Arrays.copyOfRange(input, 1, input.length);

        boolean curTableExists = false;

        if (curTableDir.exists()) {
            List<Class<?>> signatureTypes = AbstractStoreable.readTypes(curTableDir.toString());
            String[] curTypes = new String[signatureTypes.size()];

            for (int i = 0; i < signatureTypes.size(); i++) {
                curTypes[i] = ColumnTypes.typeToName(signatureTypes.get(i));
            }

            if (Arrays.equals(curTypes, columnTypes)) {
                curTableExists = true;

                System.out.println(tableName + " exists");
            }
        }

        if (!curTableExists || !curTableDir.exists()) {
            if (curTableDir.exists()) {
                deleteCurTableDir(curTableDir, state, tableName);
            }

            curTableDir.mkdirs();

            try {
                AbstractStoreable.writeTypes(curTableDir.toString(), columnTypes);
                state.createTable(tableName);
            } catch (Exception e) {
                File signatureFile = new File(curTableDir, "signature.tsv");

                signatureFile.delete();
                curTableDir.delete();

                throw e;
            }

            System.out.println("created");
        }
    }

    private String[] checkInput(String[] input, File curTableDir) {
        if (input.length < 2) {
            ifWrongType(curTableDir);
        }

        if (!input[1].contains("(") || !input[input.length - 1].contains(")")) {
            ifWrongType(curTableDir);
        }

        input[1] = input[1].replace("(", "");
        input[input.length - 1] = input[input.length - 1].replace(")", "");

        if (input[1].trim().isEmpty()) {
            ifWrongType(curTableDir);
        }

        for (int i = 1; i < input.length; i++) {
            if (!input[i].trim().matches("(boolean|byte|double|float|int|long|String)")) {
                ifWrongType(curTableDir);
            }
        }

        return input;
    }

    private void deleteCurTableDir(File curTableDir, StoreableState state, String tableName) throws IOException {
        if (curTableDir.listFiles() != null) {
            for (File dir : curTableDir.listFiles()) {
                if (dir.listFiles() != null) {
                    for (File file : dir.listFiles()) {
                        file.delete();
                    }
                }

                dir.delete();
            }
        }

        StoreableTable curTable = state.getCurTable();

        if (curTable != null && tableName.equals(curTable.getCurTableDir().getName())) {
            curTable.clearContentAndDiff();
            state.setCurTable(null);
        }

        curTableDir.delete();
        state.removeTable(curTableDir.toString());
    }

    private void ifWrongType(File curTableDir) {
        curTableDir.delete();

        System.out.println("wrong type (CREATE ERROR: no signature)");
        throw new IllegalArgumentException("CREATE ERROR: no signature");
    }
}
