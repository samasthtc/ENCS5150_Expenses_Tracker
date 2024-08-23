package edu.birzeit.expensesTracker.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import edu.birzeit.expensesTracker.Database.DataBaseHelper;
import edu.birzeit.expensesTracker.Database.ExpenseTable;
import edu.birzeit.expensesTracker.Database.ExpenseTypeTable;
import edu.birzeit.expensesTracker.Models.Expense;
import edu.birzeit.expensesTracker.Models.ExpenseAdapter;
import edu.birzeit.expensesTracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFS_NAME = "FontPreferences";
    private static final String FONT_SIZE_KEY = "font_size_key";
    private static final float DEFAULT_FONT_SIZE = 14f;
    private DataBaseHelper dbHelper;
    private ListView listView;

    private String mParam1;
    private String mParam2;

    public ListFragment() {
    }

    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static int getTotalHeight(View listItem, ListAdapter listAdapter) {
        int itemHeight = listItem.getMeasuredHeight();
        int itemCount = listAdapter.getCount();
        int visibleItems;

        if (itemCount >= 4)
            visibleItems = 3;
        else
            visibleItems = itemCount;

        int totalHeight = itemHeight * visibleItems;

        if (itemCount >= 4)
            totalHeight += (int) (itemHeight / 1.75);

        return totalHeight;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DataBaseHelper(getContext());
        listView = view.findViewById(R.id.listView);
        loadExpenses();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Expense selectedExpense = (Expense) listView.getItemAtPosition(position);
            if (selectedExpense.getId() != -1)
                showDetails(selectedExpense);
        });
    }

    public void loadExpenses() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();

            String query = "SELECT e." + ExpenseTable.COLUMN_ID + ", " +
                    "et." + ExpenseTypeTable.COLUMN_TYPE_NAME + ", " +
                    "e." + ExpenseTable.COLUMN_AMOUNT + ", " +
                    "e." + ExpenseTable.COLUMN_DATE + ", " +
                    "e." + ExpenseTable.COLUMN_NOTE +
                    " FROM " + ExpenseTable.TABLE_NAME + " e " +
                    "JOIN " + ExpenseTypeTable.TABLE_NAME + " et ON e." + ExpenseTable.COLUMN_TYPE_ID + " = et." + ExpenseTypeTable.COLUMN_ID;

            cursor = db.rawQuery(query, null);

            ArrayList<Expense> expenses = new ArrayList<>();

            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            float fontSize = prefs.getFloat(FONT_SIZE_KEY, DEFAULT_FONT_SIZE);

            int idColumnIndex = cursor.getColumnIndex(ExpenseTable.COLUMN_ID);
            int typeColumnIndex = cursor.getColumnIndex(ExpenseTypeTable.COLUMN_TYPE_NAME);
            int amountColumnIndex = cursor.getColumnIndex(ExpenseTable.COLUMN_AMOUNT);
            int dateColumnIndex = cursor.getColumnIndex(ExpenseTable.COLUMN_DATE);

            if (idColumnIndex == -1 || typeColumnIndex == -1 || amountColumnIndex == -1 || dateColumnIndex == -1) {
                expenses.add(new Expense(-1, -1, 0, "Error: Missing data", "", ""));
            } else {
                if (cursor.moveToLast()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseTable.COLUMN_ID));
                        String type = cursor.getString(typeColumnIndex);
                        double amount = cursor.getDouble(amountColumnIndex);
                        String date = cursor.getString(dateColumnIndex);
                        String note = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTable.COLUMN_NOTE));

                        Expense expense = new Expense(id, typeColumnIndex, amount, note, date, type);
                        expenses.add(expense);
                    } while (cursor.moveToPrevious());
                } else {
                    expenses.add(new Expense(-1, -1, 0, "No expenses found", "", ""));
                }
            }
            ExpenseAdapter adapter = new ExpenseAdapter(getContext(), expenses, fontSize);
            listView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listView);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        View listItem = listAdapter.getView(0, null, listView);
        listItem.measure(0, 0);
        int totalHeight = getTotalHeight(listItem, listAdapter);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                totalHeight
        );
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void showDetails(Expense expense) {
        DetailsFragment detailsFragment = DetailsFragment.newInstance(expense);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.details_fragment, detailsFragment);
        transaction.commit();
    }

    public interface communicator {
        void onExpenseAddedOrRemoved();
    }
}