package edu.birzeit.expensesTracker.Database;

public class ExpenseTypeTable {
    public static final String TABLE_NAME = "expense_types";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE_NAME = "type_name";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TYPE_NAME + " TEXT NOT NULL" +
            ");";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

