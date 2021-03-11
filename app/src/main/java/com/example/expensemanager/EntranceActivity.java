package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class EntranceActivity extends AppCompatActivity implements CalenderFragment.OnMessageTransaction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addFragment();
    }

    private void addFragment()
    {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("Hi","Blade");
        CalenderFragment calenderFragment = new CalenderFragment();
        calenderFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fragmentContainer, calenderFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onMessageSend(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}