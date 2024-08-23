package edu.birzeit.expensesTracker.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.birzeit.expensesTracker.R;

public class ExpenseAdapter extends ArrayAdapter<Expense> {
    private final float fontSize;

    public ExpenseAdapter(@NonNull Context context, @NonNull ArrayList<Expense> expenses, float fontSize) {
        super(context, 0, expenses);
        this.fontSize = fontSize;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Expense expense = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(expense.toString());
        textView.setTextSize(fontSize);

        return convertView;
    }
}
