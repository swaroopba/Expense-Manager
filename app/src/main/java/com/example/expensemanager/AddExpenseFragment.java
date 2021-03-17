package com.example.expensemanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseFragment extends Fragment {

    private final String kDateString = "DateString";
    private final String kExpenseList = "ExpenseList";

    private String date;
    private Integer dayOfMonth;
    private Integer month;
    private Integer year;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = getArguments().getString(kDateString);

            String dateSplit[] = date.split("/");
            dayOfMonth = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]);
            year = Integer.parseInt(dateSplit[2]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        TextView dateText = view.findViewById(R.id.dateStr);
        String dateTextValue = dayOfMonth.toString()+" "+getMonthName(month)+" "+year.toString();
        dateText.setText(dateTextValue);

        EditText amountText = view.findViewById(R.id.amount);
        EditText newTag = view.findViewById(R.id.newTag);

        ArrayList<String> tagData = new ArrayList<String>();
        tagData.add("Choose One");
        tagData.add("Add New");
        tagData.add("Green");
        tagData.add("Red");
        tagData.add("Orange");
        tagData.add("Blue");


        ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tagData);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner tagSpinner = view.findViewById(R.id.tag);
        tagSpinner.setAdapter(tagAdapter);

        MultiAutoCompleteTextView commentText = view.findViewById(R.id.comment);

        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1)
                {
                    tagSpinner.setVisibility(View.GONE);
                    newTag.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("ERROR","Nothing did");
            }
        });

        Button addButton = view.findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("ERROR", "data adding");

                String date = dateTextValue;
                Double amount = 0.0;
                amount = Double.parseDouble(amountText.getText().toString());

                String tag = "";
                if ( tagSpinner.getVisibility() == View.VISIBLE )
                {
                    tag = tagSpinner.getSelectedItem().toString();
                }
                else{
                    tag = newTag.getText().toString();
                }

                String comment = commentText.getText().toString();

                addToDataBase(date, amount, tag, comment);
            }
        });

        Button cancelButton = view.findViewById(R.id.cancelBtn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragment = getFragmentManager();
                fragment.popBackStackImmediate();
            }
        });

        return view;
    }

    private void addToDataBase(String date, Double amount, String tag, String comment)
    {
            ExpenseEvent newEvent = new ExpenseEvent(date, amount, tag, comment);
            myRef.child("ExpenseEvent").push().setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getContext(), "Added data successfully!!", Toast.LENGTH_SHORT).show();

//                    DisplayExpenseFragment frag = new DisplayExpenseFragment();
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.fragmentContainer, frag);
//                    ft.addToBackStack("addExpense");
//                    ft.commit();
                }
            });
    }

    private String getMonthName(Integer num)
    {
        String res = "";
        switch (num)
        {
            case 1: res = "Jan"; break;
            case 2: res = "Feb"; break;
            case 3: res = "Mar"; break;
            case 4: res = "Apr"; break;
            case 5: res = "May"; break;
            case 6: res = "June"; break;
            case 7: res = "July"; break;
            case 8: res = "Aug"; break;
            case 9: res = "Sep"; break;
            case 10: res = "Oct"; break;
            case 11: res = "Nov"; break;
            case 12: res = "Dec"; break;
        }
        return res;
    }
}