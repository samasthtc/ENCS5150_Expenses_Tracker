package edu.birzeit.expensesTracker.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.birzeit.expensesTracker.Database.DataBaseHelper;
import edu.birzeit.expensesTracker.Models.Expense;
import edu.birzeit.expensesTracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    private static final String ARG_EXPENSE = "expense";
    private static final String PREFS_NAME = "FontPreferences";
    private static final String FONT_SIZE_KEY = "font_size_key";
    private static final float DEFAULT_FONT_SIZE = 14f;
    private Expense expense;
    private DataBaseHelper dbHelper;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(Expense expense) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EXPENSE, expense);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expense = getArguments().getParcelable(ARG_EXPENSE);
        }
        dbHelper = new DataBaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final float[] currentFontSize = {getFontSize()};
        setFontSizeToAllViews(view, currentFontSize[0]);

        ImageButton plusButton = view.findViewById(R.id.imageButtonPlus);
        ImageButton minusButton = view.findViewById(R.id.imageButtonMinus);

        TextView fontSizeDisplay = view.findViewById(R.id.textViewFont);
        fontSizeDisplay.setText(String.format("%.1f", currentFontSize[0]));

        TextView textViewType = view.findViewById(R.id.textViewType);
        TextView textViewAmount = view.findViewById(R.id.textViewAmount);
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        TextView editTextNotes = view.findViewById(R.id.editTextNotes);
        ImageButton imageButtonDelete = view.findViewById(R.id.imageButtonDelete);

        plusButton.setOnClickListener(v -> {
            currentFontSize[0] += 1;
            setFontSize(currentFontSize[0]);
            fontSizeDisplay.setText(String.format("%.1f", currentFontSize[0]));
            setFontSizeToAllViews(view, currentFontSize[0]);
            updateListFragmentFontSize();
            updateAddFragmentFontSize();

        });

        minusButton.setOnClickListener(v -> {
            if (currentFontSize[0] > 8) {
                currentFontSize[0] -= 1;
                setFontSize(currentFontSize[0]);
                fontSizeDisplay.setText(String.format("%.1f", currentFontSize[0]));
                setFontSizeToAllViews(view, currentFontSize[0]);
                updateListFragmentFontSize();
                updateAddFragmentFontSize();
            }
        });

        if (expense != null) {
            textViewType.setText(expense.getTypeName());
            textViewAmount.setText(String.valueOf(expense.getAmount()));
            textViewDate.setText(expense.getDate());
            editTextNotes.setText(expense.getNote());

            imageButtonDelete.setOnClickListener(v -> {
                if (expense != null) {
                    dbHelper.deleteExpense(expense.getId());
                    final ListFragment.communicator communicator = (ListFragment.communicator) getActivity();
                    if (communicator != null) {
                        communicator.onExpenseAddedOrRemoved();
                    }

                    textViewType.setText("");
                    textViewAmount.setText("");
                    textViewDate.setText("");
                    editTextNotes.setText("");
                    Toast.makeText(getContext(), "Entry deleted successfully!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private float getFontSize() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(FONT_SIZE_KEY, DEFAULT_FONT_SIZE);
    }

    private void setFontSize(float fontSize) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(FONT_SIZE_KEY, fontSize);
        editor.apply();
    }

    private void setFontSizeToAllViews(View view, float fontSize) {
        TextView textViewType = view.findViewById(R.id.textViewType);
        TextView textViewAmount = view.findViewById(R.id.textViewAmount);
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        TextView textView1 = view.findViewById(R.id.textView1);
        TextView textView2 = view.findViewById(R.id.textView2);
        TextView textView3 = view.findViewById(R.id.textView3);
        TextView textView4 = view.findViewById(R.id.textView4);
        TextView editTextNotes = view.findViewById(R.id.editTextNotes);

        textViewType.setTextSize(fontSize);
        textViewAmount.setTextSize(fontSize);
        textViewDate.setTextSize(fontSize);
        textView1.setTextSize(fontSize);
        textView2.setTextSize(fontSize);
        textView3.setTextSize(fontSize);
        textView4.setTextSize(fontSize);
        editTextNotes.setTextSize(fontSize);
    }

    private void updateListFragmentFontSize() {
        if (getActivity() instanceof ListFragment.communicator) {
            ((ListFragment.communicator) getActivity()).onExpenseAddedOrRemoved();
        }
    }

    private void updateAddFragmentFontSize() {
        if (getActivity() instanceof AddFragment.communicator) {
            ((AddFragment.communicator) getActivity()).onFontSizeChange();
        }

    }
}