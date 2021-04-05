package com.example.expensemanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    private String m_dateString;
    private Boolean m_isItCurrentDate;
    private OnMessageTransaction m_onMessageTransaction;
    private Integer m_date;
    private Integer m_month;
    private Integer m_year;
    private Integer m_numDays;
    private Integer m_screenWidth;
    private Integer m_screenHeight;
    private Integer m_currentDate;

    public CalenderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;
        try {
            m_onMessageTransaction = (OnMessageTransaction)activity;
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
            m_dateString = getArguments().getString(kDateString);
            m_isItCurrentDate = getArguments().getBoolean(kIsItCurrentDate);
        }

        Point screenSize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);
        m_screenWidth = screenSize.x;
        m_screenHeight = screenSize.y;

        Calendar cal = Calendar.getInstance();
        m_currentDate = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calender, container, false);

        String dateList[] = m_dateString.split("/");
        m_date = Integer.parseInt(dateList[0]);
        m_month = Integer.parseInt(dateList[1]);
        m_year = Integer.parseInt(dateList[2]);

        Calendar c2 = Calendar.getInstance();
        c2.set(m_year, m_month -1, m_date);
        c2.set(Calendar.DAY_OF_MONTH, 1);
        Integer startDateMonth = c2.get(Calendar.DAY_OF_WEEK);
        Integer totalDaysInMonth = c2.getActualMaximum(Calendar.DAY_OF_MONTH);


        m_numDays = 1;
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
                String tempStr = m_numDays.toString();
                if(m_numDays == m_currentDate)
                {
                    refButton.setBackgroundColor(getResources().getColor(R.color.purple_200));
                }
                refButton.setText(tempStr);
                refButton.setTextSize(m_screenWidth*0.015f);
                refButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDate(refButton.getText().toString());
                    }
                });
                m_numDays = m_numDays + 1;
            }
        }


        for(Integer i = 1;i <= 7; i++ ) {
            String id = "Day" + i;
            Button refButton = view.findViewById(getResources().getIdentifier(id, "id", getContext().getPackageName()));
            refButton.setTextSize(m_screenWidth * 0.015f);
        }

        Button tempDate = (Button) view.findViewById(R.id.date);
        tempDate.setText(getMonthName(m_month)+" "+ m_year);
        tempDate.setTextSize(m_screenWidth*0.02f);
        tempDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_onMessageTransaction.onMonthYearPressed(m_date +"/"+ m_month +"/"+ m_year);
            }
        });

        Button lastButton = view.findViewById(R.id.last);
        lastButton.setTextSize(m_screenWidth*0.02f);
        lastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_onMessageTransaction.onPreviousMonthPressed(m_date +"/"+ m_month +"/"+ m_year);
            }
        });

        Button nextButton = view.findViewById(R.id.next);
        nextButton.setTextSize(m_screenWidth*0.02f);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_onMessageTransaction.onNextMonthPressed(m_date +"/"+ m_month +"/"+ m_year);
            }
        });

        return view;
    }

    private void sendDate(String day)
    {
        String date = day + "/" + m_month + "/" + m_year;
        m_onMessageTransaction.onDatePressed(date);
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