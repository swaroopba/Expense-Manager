package com.example.expensemanager;

public class ExpenseEvent {

    private String date;
    private Double amount;
    private String tag;
    private String comment;

    public ExpenseEvent()
    {

    }

    public ExpenseEvent(String date, Double amount, String tag, String comment) {
        this.date = date;
        this.amount = amount;
        this.tag = tag;
        this.comment = comment;
    }

    public String getDate() {
        return date;
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
