package ru.fizteh.fivt.students.vyatkina.database.superior;

public interface TableProviderConstants {

    public static final String UNSUPPORTED_TABLE_NAME = "Unsupported table name";
    public static final String IS_NOT_A_DIRECTORY = " is not a directory";
    public static final String IS_NOT_A_FILE = " is not a file";
    public static final String FILE_OR_DIRECTORY_DOES_NOT_EXIST = " file or directory does not exist";
    public static final String TABLE_NOT_EXIST = "Table not exist";
    public static final String UNEXPECTED_CLASS_IN_STORABLE = "Unexpected class in Storable. ";
    public static final String EMPTY_DIRECTORY = "Empty table directory.";
    public static final String BAD_FILE_NAME = "Bad file name";
    public static final String EXPECTED = "Expected : ";
    public static final String BUT_HAVE = " but have: ";
    public static final int MAX_SUPPORTED_NAME_LENGTH = 1024;
    public static final int NUMBER_OF_FILES = 16;
    public static final int NUMBER_OF_DIRECTORIES = 16;
    public static final String DOT_DIR = ".dir";
    public static final String DOT_DAT = ".dat";
    public static final String SIGNATURE_FILE = "signature.tsv";
    public static final String PROPERTY_DIRECTORY = "fizteh.db.dir";
    public static final String MAC_DS_FILE = ".DS_Store";
}
