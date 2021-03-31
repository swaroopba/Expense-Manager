package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class EntranceActivity extends AppCompatActivity implements CalenderFragment.OnMessageTransaction {

    private final String kDateString = "DateString";
    private final String kIsItCurrentDate = "IsItCurrentDate";
    private final String kStartDate = "StartDate";
    private final String kEndDate = "EndDate";
    private final String kTag = "Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("ERROR","EntranceActivity onCreate");

        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
        addFragment(date, true, false);
    }

    @Override
    public void onPreviousMonthPressed(String message) {
        Log.d("ERROR", message);
        ArrayList<Integer> current = getDateFromString(message);

        Integer currentMonth = current.get(1);
        Integer prevMonth;
        if (currentMonth == 1)
        {
               prevMonth = 12;
               current.set(2, current.get(2)-1);
        }
        else {
            prevMonth = currentMonth - 1;
        }
        addFragment(current.get(0)+"/"+prevMonth+"/"+current.get(2), false, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);;
        switch (item.getItemId())
        {
            case R.id.calender:
                addFragment(date, true, false);
                break;
            case R.id.addExpense:
                onDatePressed(date);
                break;
            case R.id.viewExpense:
                DisplayExpenseFragment frag = new DisplayExpenseFragment();
                Bundle bundle = new Bundle();
                bundle.putString(kStartDate, "");
                bundle.putString(kEndDate, "");
                bundle.putString(kTag, "");

                frag.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentContainer, frag);
                ft.addToBackStack("addExpense");
                ft.commit();
                break;

        }

        return true;
    }

    @Override
    public void onNextMonthPressed(String message) {
        ArrayList<Integer> current = getDateFromString(message);

        Integer currentMonth = current.get(1);
        Integer nextMonth;
        if (currentMonth == 12)
        {
            nextMonth = 1;
            current.set(2, current.get(2)+1);
        }
        else{
            nextMonth = currentMonth + 1;
        }

        addFragment(current.get(0)+"/"+nextMonth+"/"+current.get(2), false, true);

    }

    @Override
    public void onDatePressed(String date) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString(kDateString, date);

        AddExpenseFragment addFragment = new AddExpenseFragment();
        addFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragmentContainer, addFragment);
        fragmentTransaction.addToBackStack("addFragment");
        fragmentTransaction.commit();

    }

    private ArrayList<Integer> getDateFromString(String date)
    {
        String str[] = date.split("/");
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for(String i: str)
        {
            ret.add(Integer.parseInt(i));
        }
        return  ret;
    }

    private void addFragment(String date, Boolean isCurrentDate, Boolean isReplaceFragment)
    {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString(kDateString, date);
        bundle.putBoolean(kIsItCurrentDate, isCurrentDate);

        CalenderFragment calenderFragment = new CalenderFragment();
        calenderFragment.setArguments(bundle);
        if(!isReplaceFragment) {
            fragmentTransaction.add(R.id.fragmentContainer, calenderFragment);
        }else{
            fragmentTransaction.replace(R.id.fragmentContainer, calenderFragment);
        }
        fragmentTransaction.commit();

    }

    @Override
    public void onMonthYearPressed(String date) {

        Log.d("ERROR", "onMonthYearPressed->"+date);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString(kDateString, date);

        DateSelectionFragment dateSelectionFragment = new DateSelectionFragment();
        dateSelectionFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragmentContainer, dateSelectionFragment);
        fragmentTransaction.addToBackStack("datePickerFragment");
        fragmentTransaction.commit();
    }
}