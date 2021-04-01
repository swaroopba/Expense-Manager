package com.example.expensemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class AddExpenseFragment extends Fragment {

    private final String kDateString = "DateString";
    private final String kExpenseList = "ExpenseList";
    private final String kStartDate = "StartDate";
    private final String kEndDate = "EndDate";
    private final String kTag = "Tag";
    final private String kSignInName = "SignInName";
    final private String kSignInEmail = "SignInEmail";
    final private String kSignInId = "SignInID";
    final private String kSharedPrefName = "com.example.ExpenseManager";

    private String date;
    private Integer dayOfMonth;
    private Integer month;
    private Integer year;
    private Set<String> tagSet;
    private Boolean areAllTagsRead;
    private String signInEmail;
    private Integer m_screenWidth;
    private Integer m_screenHeight;

    Spinner tagSpinner;
    ArrayList<String> tagData;

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

            tagSet = new HashSet<>();
            areAllTagsRead = false;

            Point screenSize = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);
            m_screenWidth = screenSize.x;
            m_screenHeight = screenSize.y;

            String dateSplit[] = date.split("/");
            dayOfMonth = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]);
            year = Integer.parseInt(dateSplit[2]);

            SharedPreferences sp = getActivity().getSharedPreferences(kSharedPrefName, Context.MODE_PRIVATE);
            signInEmail = sp.getString(kSignInId, "");
            Log.d("ERROR", "Signed email->"+signInEmail);
            myRef = database.getReference("Users");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        TextView dateText = view.findViewById(R.id.dateStr);
        String dateTextValue = dayOfMonth.toString()+" "+getMonthName(month)+" "+year.toString();
        dateText.setText(dateTextValue);
        dateText.setTextSize(m_screenWidth*0.03f);

        TextView amountStr = view.findViewById(R.id.amountStr);
        TextView tagStr = view.findViewById(R.id.tagStr);
        TextView commentStr = view.findViewById(R.id.commentStr);
        commentStr.setTextSize(m_screenWidth*0.02f);
        amountStr.setTextSize(m_screenWidth*0.02f);
        tagStr.setTextSize(m_screenWidth*0.02f);


        EditText amountText = view.findViewById(R.id.amount);
        EditText newTag = view.findViewById(R.id.newTag);

        tagData = new ArrayList<String>();
        tagData.add("Choose One");
        tagData.add("Add New");

        getAvailableTags();


            Iterator<String> tagIter = tagSet.iterator();
            while (tagIter.hasNext()) {
                String tempTag = tagIter.next();
                if (!tempTag.equals("Choose One")) {
                    tagData.add(tempTag);
                }
            }

        ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tagData);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner = view.findViewById(R.id.tag);
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
        addButton.setTextSize(m_screenWidth*0.015f);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ERROR","Add click reached");
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
        cancelButton.setTextSize(m_screenWidth*0.015f);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragment = getFragmentManager();
                fragment.popBackStackImmediate();
            }
        });

        return view;
    }

    private void getAvailableTags()
    {
        myRef.child(signInEmail).child(kExpenseList).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iter = snapshot.getChildren();
                for(DataSnapshot obj: iter)
                {
                    ExpenseEvent post = obj.getValue(ExpenseEvent.class);
                    tagSet.add(post.getTag());
                }

                Iterator<String> tagIter = tagSet.iterator();
                while (tagIter.hasNext()) {
                    String tempTag = tagIter.next();
                    if (!tempTag.equals("Choose One")) {
                        tagData.add(tempTag);
                    }
                }

                ArrayAdapter<String> newTagAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tagData);
                newTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(newTagAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToDataBase(String date, Double amount, String tag, String comment)
    {
            ExpenseEvent newEvent = new ExpenseEvent(date, amount, tag, comment);
            myRef.child(signInEmail).child(kExpenseList).push().setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getContext(), "Added data successfully!!", Toast.LENGTH_SHORT).show();

                    DisplayExpenseFragment frag = new DisplayExpenseFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(kStartDate, "");
                    bundle.putString(kEndDate, "");
                    bundle.putString(kTag, "");

                    frag.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentContainer, frag);
                    ft.addToBackStack("addExpense");
                    ft.commit();
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