package com.example.expensemanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

public class DateSelectionFragment extends Fragment {

    private final String kDateString = "DateString";
    private final String kIsFilter = "IsFilter";
    private final String kChooseOption = "ChooseOption";
    private final String kStartDate = "StartDate";
    private final String kEndDate = "EndDate";
    private final String kTag = "Tag";
    //private final String kIsItCurrentDate = "IsItCurrentDate";

    private String dateString;
    //private Boolean isItCurrentDate;
    private Integer date;
    private Integer month;
    private Integer year;
    private Integer totalDays;

    private TextView dateText;
    private TextView monthText;
    private TextView yearText;
    private ImageButton up1;
    private Boolean isCalledForFiltering;
    private String tag;
    private String startDate;
    private String endDate;
    private String chooseOption;

    public DateSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dateString = getArguments().getString(kDateString);
            isCalledForFiltering = getArguments().getBoolean(kIsFilter);
            if (isCalledForFiltering)
            {
                tag = getArguments().getString(kTag);
                startDate = getArguments().getString(kStartDate);
                endDate = getArguments().getString(kEndDate);
                chooseOption = getArguments().getString(kChooseOption);
            }

            String dateStr[] = dateString.split("/");
            date = Integer.parseInt(dateStr[0]);
            month = Integer.parseInt(dateStr[1]);
            year = Integer.parseInt(dateStr[2]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_selection, container, false);
        dateText = view.findViewById(R.id.dateText);
        monthText = view.findViewById(R.id.monthText);
        yearText = view.findViewById(R.id.yearText);

        up1 = (ImageButton) view.findViewById(R.id.up1);
        ImageButton up2 = view.findViewById(R.id.up2);
        ImageButton up3 = view.findViewById(R.id.up3);
        ImageButton down1 = view.findViewById(R.id.down1);
        ImageButton down2 = view.findViewById(R.id.down2);
        ImageButton down3 = view.findViewById(R.id.down3);

        dateText.setText(date.toString());
        monthText.setText(month.toString());
        yearText.setText(year.toString());
        setMaxDaysInMonth(month, year);

        up1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer date = Integer.parseInt(dateText.getText().toString());
                if (date != totalDays)
                {
                    date = date + 1;
                    if (date == totalDays)
                    {
                        up1.setEnabled(false);
                        up1.setBackground(getResources().getDrawable(R.drawable.disabled_button));
                    }

                    if (date != 1)
                    {
                        down1.setEnabled(true);
                        down1.setBackground(getResources().getDrawable(R.drawable.custom_button));
                    }
                    dateText.setText(date.toString());
                }
            }
        });

        up2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer month = Integer.parseInt(monthText.getText().toString());
                if (month != 12){
                    month = month + 1;
                    if (month == 12)
                    {
                        up2.setEnabled(false);
                        up2.setBackground(getResources().getDrawable(R.drawable.disabled_button));
                    }

                    if (month != 1)
                    {
                        down2.setEnabled(true);
                        down2.setBackground(getResources().getDrawable(R.drawable.custom_button));
                    }
                    monthText.setText(month.toString());

                    Integer year = Integer.parseInt(yearText.getText().toString());
                    setMaxDaysInMonth(month, year);
                }

            }
        });

        up3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer year = Integer.parseInt(yearText.getText().toString());
                year = year + 1;
                yearText.setText(year.toString());

                Integer month = Integer.parseInt(monthText.getText().toString());
                setMaxDaysInMonth(month, year);
            }
        });

        down1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer date = Integer.parseInt(dateText.getText().toString());
                if (date != 0){
                    date = date - 1;
                    if (date == 1)
                    {
                        down1.setEnabled(false);
                        down1.setBackground(getResources().getDrawable(R.drawable.disabled_button));
                    }

                    if (date != totalDays)
                    {
                        up1.setEnabled(true);
                        up1.setBackground(getResources().getDrawable(R.drawable.custom_button));
                    }
                    dateText.setText(date.toString());
                }
            }
        });

        down2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer month = Integer.parseInt(monthText.getText().toString());
                if (month != 0){
                    month = month - 1;
                    if (month == 1)
                    {
                        down2.setEnabled(false);
                        down2.setBackground(getResources().getDrawable(R.drawable.disabled_button));
                    }

                    if (month != 12)
                    {
                        up2.setEnabled(true);
                        up2.setBackground(getResources().getDrawable(R.drawable.custom_button));
                    }
                    monthText.setText(month.toString());

                    Integer year = Integer.parseInt(yearText.getText().toString());
                    setMaxDaysInMonth(month, year);
                }

            }
        });

        down3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer year = Integer.parseInt(yearText.getText().toString());
                year = year - 1;
                yearText.setText(year.toString());

                Integer month = Integer.parseInt(monthText.getText().toString());
                setMaxDaysInMonth(month, year);
            }
        });

        Button selectBtn = view.findViewById(R.id.selectButton);
        Button cancelBtn = view.findViewById(R.id.cancelButton);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Bundle bundle = new Bundle();

                if (!isCalledForFiltering) {
                    AddExpenseFragment addFragment = new AddExpenseFragment();
                    bundle.putString(kDateString, (dateText.getText().toString()+"/"+monthText.getText().toString()+"/"+yearText.getText().toString()));
                    addFragment.setArguments(bundle);

                    ft.addToBackStack("Sample");
                    ft.replace(R.id.fragmentContainer, addFragment);
                    ft.commit();
                }
                else {

                    if (chooseOption == kStartDate)
                    {
                        startDate = (dateText.getText().toString()+"/"+monthText.getText().toString()+"/"+yearText.getText().toString());
                    }
                    else
                    {
                        endDate = (dateText.getText().toString()+"/"+monthText.getText().toString()+"/"+yearText.getText().toString());
                    }

                    DisplayExpenseFragment displayFragment = new DisplayExpenseFragment();
                    bundle.putString(kStartDate, startDate);
                    bundle.putString(kEndDate, endDate);
                    bundle.putString(kTag, tag);
                    displayFragment.setArguments(bundle);

                    ft.addToBackStack("Sample");
                    ft.replace(R.id.fragmentContainer, displayFragment);
                    ft.commit();
                }

            }
        });

        return view;
    }

    private void setMaxDaysInMonth(Integer month, Integer year)
    {
        Calendar cal= Calendar.getInstance();
        cal.set(year, month-1, 1);
        totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Integer currentDate = Integer.parseInt(dateText.getText().toString());
        if (currentDate > totalDays)
        {
            dateText.setText(totalDays.toString());
        }
        else if (currentDate < totalDays){
            up1.setEnabled(true);
        }

    }
}