package edu.birzeit.expensesTracker.Database;

public class ExpenseTable {
    public static final String TABLE_NAME = "expenses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE_ID = "type_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_DATE = "date";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TYPE_ID + " INTEGER NOT NULL, " +
            COLUMN_AMOUNT + " REAL, " +
            COLUMN_NOTE + " TEXT, " +
            COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (" + COLUMN_TYPE_ID + ") REFERENCES " + ExpenseTypeTable.TABLE_NAME + "(" + ExpenseTypeTable.COLUMN_ID + ")" +
            ");";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

