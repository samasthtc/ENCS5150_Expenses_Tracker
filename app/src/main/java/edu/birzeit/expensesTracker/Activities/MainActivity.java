package edu.birzeit.expensesTracker.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import edu.birzeit.expensesTracker.Fragments.AddFragment;
import edu.birzeit.expensesTracker.Fragments.DetailsFragment;
import edu.birzeit.expensesTracker.Fragments.ListFragment;
import edu.birzeit.expensesTracker.R;

public class MainActivity extends AppCompatActivity implements ListFragment.communicator, AddFragment.communicator {

    private static final String PREFS_NAME = "FontPreferences";
    private static final String FONT_SIZE_KEY = "font_size_key";
    private static final float DEFAULT_FONT_SIZE = 14f; // Default font size

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, new AddFragment(), "add_fragment");
        transaction.replace(R.id.fragmentContainerView2, new ListFragment(), "list_fragment");
        transaction.replace(R.id.fragmentContainerView3, new DetailsFragment(), "details_fragment");
        transaction.commit();
    }

    @Override
    public void onExpenseAddedOrRemoved() {
        ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        if (listFragment != null) {
            listFragment.loadExpenses();
        }
    }

    @Override
    public void onFontSizeChange() {
        AddFragment addFragment = (AddFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        float fontSize = prefs.getFloat(FONT_SIZE_KEY, DEFAULT_FONT_SIZE);
        if (addFragment != null) {
            addFragment.setFontSizeToAllViews(fontSize);
        }
    }
}