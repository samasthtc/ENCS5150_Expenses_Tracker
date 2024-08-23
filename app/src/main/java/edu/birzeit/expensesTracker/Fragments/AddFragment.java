package edu.birzeit.expensesTracker.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.birzeit.expensesTracker.Database.DataBaseHelper;
import edu.birzeit.expensesTracker.Database.ExpenseTable;
import edu.birzeit.expensesTracker.Database.ExpenseTypeTable;
import edu.birzeit.expensesTracker.Models.CustomArrayAdapter;
import edu.birzeit.expensesTracker.Models.Expense;
import edu.birzeit.expensesTracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFS_NAME = "FontPreferences";
    private static final String FONT_SIZE_KEY = "font_size_key";
    private static final float DEFAULT_FONT_SIZE = 14f;
    private View viewFragment;
    private Spinner typeSpinner;
    private DataBaseHelper dbHelper;
    private String mParam1;
    private String mParam2;

    public AddFragment() {
    }

    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewFragment = view;

        final float currentFontSize = getFontSize();


        dbHelper = new DataBaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        typeSpinner = view.findViewById(R.id.spinner);

        Cursor cursor = db.rawQuery("SELECT " + ExpenseTypeTable.COLUMN_TYPE_NAME + " FROM " +
                ExpenseTypeTable.TABLE_NAME, null);
        ArrayList<String> types = new ArrayList<>();
        while (cursor.moveToNext()) {
            types.add(cursor.getString(cursor.getColumnIndexOrThrow(ExpenseTypeTable.COLUMN_TYPE_NAME)));
        }
        cursor.close();

        CustomArrayAdapter adapter = new CustomArrayAdapter(getContext(), R.layout.spinner_item, types, currentFontSize);
        typeSpinner.setAdapter(adapter);

        setFontSizeToAllViews(currentFontSize);

        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(v -> {
            String typeName = typeSpinner.getSelectedItem().toString();
            String amountStr = ((EditText) view.findViewById(R.id.editAmount)).getText().toString();
            String note = ((EditText) view.findViewById(R.id.editNote)).getText().toString();

            if (!amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();

                Cursor typeCursor = db1.rawQuery("SELECT " + ExpenseTypeTable.COLUMN_ID + " FROM " +
                        ExpenseTypeTable.TABLE_NAME + " WHERE " + ExpenseTypeTable.COLUMN_TYPE_NAME +
                        " = ?", new String[]{typeName});
                int typeId = -1;
                if (typeCursor.moveToFirst())
                    typeId = typeCursor.getInt(typeCursor.getColumnIndexOrThrow(ExpenseTypeTable.COLUMN_ID));

                typeCursor.close();

                if (typeId != -1) {
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    Expense expense = new Expense(-1, typeId, amount, note, date, typeName);

                    db1.execSQL("INSERT INTO " + ExpenseTable.TABLE_NAME + " (" +
                                    ExpenseTable.COLUMN_TYPE_ID + ", " +
                                    ExpenseTable.COLUMN_AMOUNT + ", " +
                                    ExpenseTable.COLUMN_NOTE + ", " +
                                    ExpenseTable.COLUMN_DATE + ") VALUES (?, ?, ?, ?)",
                            new Object[]{expense.getTypeId(), expense.getAmount(), expense.getNote(), expense.getDate()});

                    final ListFragment.communicator communicator = (ListFragment.communicator) getActivity();
                    if (communicator != null) {
                        communicator.onExpenseAddedOrRemoved();
                    }

                    Toast.makeText(getContext(), "Expense added successfully", Toast.LENGTH_SHORT).show();
                    ((EditText) view.findViewById(R.id.editAmount)).setText("");
                    ((EditText) view.findViewById(R.id.editNote)).setText("");

                } else {
                    Toast.makeText(getContext(), "Invalid expense type", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float getFontSize() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(FONT_SIZE_KEY, DEFAULT_FONT_SIZE);
    }

    public void setFontSizeToAllViews(float fontSize) {
        EditText editAmount = viewFragment.findViewById(R.id.editAmount);
        TextView textViewNote = viewFragment.findViewById(R.id.textViewNote);
        EditText editNote = viewFragment.findViewById(R.id.editNote);
        Button buttonAdd = viewFragment.findViewById(R.id.buttonAdd);

        editAmount.setTextSize(fontSize);
        textViewNote.setTextSize(fontSize);
        editNote.setTextSize(fontSize);
        buttonAdd.setTextSize(fontSize);
        CustomArrayAdapter adapter = (CustomArrayAdapter) typeSpinner.getAdapter();
        if (adapter != null) {
            adapter.setFontSize(fontSize);
        }
    }

    public interface communicator {
        void onFontSizeChange();
    }
}