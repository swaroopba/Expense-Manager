package com.example.expensemanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;


public class CalenderFragment extends Fragment {

    private final String kDateString = "DateString";
    private final String kIsItCurrentDate = "IsItCurrentDate";

    public interface OnMessageTransaction
    {
        public void onMonthYearPressed(String message);
        public void onPreviousMonthPressed(String message);
        public void onNextMonthPressed(String message);
        public void onDatePressed(String message);
    }

    private String dateString;
    private Boolean isItCurrentDate;
    private OnMessageTransaction onMessageTransaction;
    private Integer date;
    private Integer month;
    private Integer year;
    private Integer numDays;

    public CalenderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;
        try {
            onMessageTransaction = (OnMessageTransaction)activity;
        }
        catch (ClassCastException e)
        {
            Log.e("Error", "Error msg->"+e.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dateString = getArguments().getString(kDateString);
            isItCurrentDate = getArguments().getBoolean(kIsItCurrentDate);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calender, container, false);

        String dateList[] = dateString.split("/");
        date = Integer.parseInt(dateList[0]);
        month = Integer.parseInt(dateList[1]);
        year = Integer.parseInt(dateList[2]);

        Calendar c2 = Calendar.getInstance();
        c2.set(year, month-1, date);
        Log.d("ERROR",c2.toString());
        c2.set(Calendar.DAY_OF_MONTH, 1);
        Integer startDateMonth = c2.get(Calendar.DAY_OF_WEEK);
        Integer totalDaysInMonth = c2.getActualMaximum(Calendar.DAY_OF_MONTH);


        numDays = 1;
        for(Integer i = 1;i <= 42; i++ )
        {
            String id = "date" + i;
            Button refButton = view.findViewById(getResources().getIdentifier(id, "id", getContext().getPackageName()));
            Integer actualId = Integer.parseInt((String) refButton.getTag());


            if (actualId < startDateMonth || actualId > (totalDaysInMonth + startDateMonth - 1))
            {
                refButton.setVisibility(View.INVISIBLE);
            }
            else
            {
                refButton.setText(numDays.toString());
                refButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDate(refButton.getText().toString());
                    }
                });
                numDays = numDays + 1;
            }
        }

        Button tempDate = (Button) view.findViewById(R.id.date);
        tempDate.setText(getMonthName(month)+" "+year);
        tempDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMessageTransaction.onMonthYearPressed("How are you");
            }
        });

        Button lastButton = view.findViewById(R.id.last);
        lastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMessageTransaction.onPreviousMonthPressed(date+"/"+month+"/"+year);
            }
        });

        Button nextButton = view.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMessageTransaction.onNextMonthPressed(date+"/"+month+"/"+year);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void sendDate(String day)
    {
        String date = day + "/" + month + "/" + year;
        onMessageTransaction.onDatePressed(date);
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