package com.example.expensemanager;

import android.util.Log;

import java.util.Calendar;

public class ExpenseEvent {

    private long date;
    private Double amount;
    private String tag;
    private String comment;
    private String date_tag;

    private Integer getMonthFromString(String monthName)
    {
        Integer monthDigit = 1;
        switch(monthName)
        {
            case "Jan": monthDigit = 0; break;
            case "Feb": monthDigit = 1; break;
            case "Mar": monthDigit = 2; break;
            case "Apr": monthDigit = 3; break;
            case "May": monthDigit = 4; break;
            case "June": monthDigit = 5; break;
            case "July": monthDigit = 6; break;
            case "Aug": monthDigit = 7; break;
            case "Sep": monthDigit = 8; break;
            case "Oct": monthDigit = 9; break;
            case "Nov": monthDigit = 10; break;
            case "Dec": monthDigit = 11; break;
        }
        return monthDigit;
    }

    private long processDateString(String date)
    {
        long epochTime = 0;
        String dateSplit[] = date.split(" ");
        Integer dateDigit = Integer.parseInt(dateSplit[0]);
        Integer yearDigit = Integer.parseInt(dateSplit[2]);
        Integer monthDigit = getMonthFromString(dateSplit[1]);

        Log.d("ERROR", "monthDigit->"+monthDigit);

        Calendar cal = Calendar.getInstance();
        cal.set(yearDigit, monthDigit, dateDigit);
        epochTime = cal.getTimeInMillis();
        return epochTime;
    }

    public ExpenseEvent()
    {

    }

    public ExpenseEvent(String date, Double amount, String tag, String comment) {

        this.date = processDateString(date);
        this.amount = amount;
        this.tag = tag;
        this.comment = comment;
        this.date_tag = this.date+"_"+tag;
    }

    public Long getDate() {
        return date;
    }

    public String getDate_tag()
    {
        return date_tag;
    }

    public Double getAmount() {
        return amount;
    }

    public String getTag() {
        return tag;
    }

    public String getComment() {
        return comment;
    }
}
