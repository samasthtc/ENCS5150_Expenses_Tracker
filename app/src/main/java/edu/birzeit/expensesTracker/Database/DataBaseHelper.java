package edu.birzeit.expensesTracker.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(ExpenseTypeTable.CREATE_TABLE);
        db.execSQL(ExpenseTable.CREATE_TABLE);
        // predefine expense types
        db.execSQL("INSERT INTO " + ExpenseTypeTable.TABLE_NAME + " (" + ExpenseTypeTable.COLUMN_TYPE_NAME + ") VALUES " +
                "('Rent'), ('Groceries'), ('Entertainment'), " +
                "('Electricity Bills'), ('Transportation'), ('Eating Out');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ExpenseTypeTable.DROP_TABLE);
        db.execSQL(ExpenseTable.DROP_TABLE);
        onCreate(db);
    }

    public void deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ExpenseTable.TABLE_NAME, ExpenseTable.COLUMN_ID + "=?", new String[]{String.valueOf(expenseId)});
        db.close();
    }
}
