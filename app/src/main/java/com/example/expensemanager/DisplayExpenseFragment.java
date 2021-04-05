package com.example.expensemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DisplayExpenseFragment extends Fragment {

    private final String kDateString = "DateString";
    private final String kIsFilter = "IsFilter";
    private final String kChooseOption = "ChooseOption";
    private final String kStartDate = "StartDate";
    private final String kEndDate = "EndDate";
    private final String kTag = "Tag";
    final private String kSignInEmail = "SignInEmail";
    final private String kSignInId = "SignInID";
    final private String kSharedPrefName = "com.example.ExpenseManager";
    private final String kExpenseList = "ExpenseList";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    Integer screenWidth;
    Integer screenHeight;
    String startDate;
    String endDate;
    String tag;
    ArrayList<String> tagData;
    Query queryToListen;
    Integer previousTagIndex;
    Integer numOfElements;
    Boolean checkTag;
    Long lastDateTime;
    Long firstDateTime;
    private Set<String> selectedItemToDelete;
    private Set<String> tagSet;
    private String signInEmail;
    Boolean isQueryHasNoChildren;
    ValueEventListener valueEventListener;
    ValueEventListener oldListener;
    Boolean isScrollDoneByUser;
    Integer isTopReached, isBottomReached;

    public DisplayExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startDate = "";
        endDate = "";
        tag = "";
        isQueryHasNoChildren = false;

        tagSet = new HashSet<>();
        selectedItemToDelete = new HashSet<>();

        startDate = getArguments().getString(kStartDate);
        endDate = getArguments().getString(kEndDate);
        tag = getArguments().getString(kTag);

        SharedPreferences sp = getActivity().getSharedPreferences(kSharedPrefName, Context.MODE_PRIVATE);
        signInEmail = sp.getString(kSignInId, "");
        myRef = database.getReference("Users/"+signInEmail+"/"+kExpenseList);

        Point screenSize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);
        screenWidth = screenSize.x;
        screenHeight = screenSize.y;
    }

    private Integer getMonthFromString(String monthName)
    {
        Integer monthDigit = 1;
        switch(monthName)
        {
            case "Jan": monthDigit = 1; break;
            case "Feb": monthDigit = 2; break;
            case "Mar": monthDigit = 3; break;
            case "Apr": monthDigit = 4; break;
            case "May": monthDigit = 5; break;
            case "June": monthDigit = 6; break;
            case "July": monthDigit = 7; break;
            case "Aug": monthDigit = 8; break;
            case "Sep": monthDigit = 9; break;
            case "Oct": monthDigit = 10; break;
            case "Nov": monthDigit = 11; break;
            case "Dec": monthDigit = 12; break;
        }
        return monthDigit;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_expense, container, false);
        LinearLayout displayLinear = (LinearLayout) view.findViewById(R.id.displayLinear);
        LinearLayout filterLinear = (LinearLayout) view.findViewById(R.id.topFilters);
        LinearLayout.LayoutParams filterParam = new LinearLayout.LayoutParams(screenWidth, screenHeight/12);
        filterLinear.setLayoutParams(filterParam);

        tagData = new ArrayList<String>();
        tagData.add("Choose One");
        Spinner tagSpinner = view.findViewById(R.id.tagFilterVal);

        myRef.orderByChild("tag").addValueEventListener(new ValueEventListener() {
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

                ArrayAdapter<String> newTagAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner, tagData);
                newTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(newTagAdapter);

                previousTagIndex = tagData.indexOf(tag);
                tagSpinner.setSelection(previousTagIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Iterator<String> tagIter = tagSet.iterator();
        while(tagIter.hasNext())
        {
            String tempTag = tagIter.next();
            if (!tempTag.equals("Choose One"))
            {
                tagData.add(tempTag);
            }
        }


        ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner, tagData);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(tagAdapter);
        if (tag != tagData.get(0))
        {
            previousTagIndex = tagData.indexOf(tag);
            tagSpinner.setSelection(previousTagIndex);
        }

        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ERROR", "Reached on spinner selected");
                if ((position != 0) && (position != previousTagIndex)) {
                    DisplayExpenseFragment frag = new DisplayExpenseFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(kStartDate, startDate);
                    bundle.putString(kEndDate, endDate);
                    bundle.putString(kTag, tagData.get(position));

                    frag.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentContainer, frag);
                    ft.addToBackStack("addExpense");
                    ft.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Button startDateBtn = view.findViewById(R.id.startDateButton);
        startDateBtn.setTextSize(screenWidth * 0.015f);
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(kDateString, date);
                bundle.putBoolean(kIsFilter, true);
                bundle.putString(kChooseOption, kStartDate);
                bundle.putString(kStartDate, startDate);
                bundle.putString(kEndDate, endDate);
                bundle.putString(kTag, tagSpinner.getSelectedItem().toString());

                Log.d("ERROR", "tag val..."+tagSpinner.getSelectedItem().toString());

                DateSelectionFragment dateSelectionFragment = new DateSelectionFragment();
                dateSelectionFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragmentContainer, dateSelectionFragment);
                fragmentTransaction.addToBackStack("datePickerFragment");
                fragmentTransaction.commit();
            }
        });


        Button deleteBut = view.findViewById(R.id.deleteMeButton);
        deleteBut.setTextSize(screenWidth * 0.02f);
        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedItemToDelete != null  && !selectedItemToDelete.isEmpty())
                {
                    Iterator<String> it = selectedItemToDelete.iterator();
                    while (it.hasNext()) {
                        DatabaseReference sampleRef = myRef.child(it.next());
                        sampleRef.removeValue();
                    }

                    // Reloading fragment
                    DisplayExpenseFragment frag = new DisplayExpenseFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(kStartDate, startDate);
                    bundle.putString(kEndDate, endDate);
                    bundle.putString(kTag, tag);

                    frag.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentContainer, frag);
                    ft.addToBackStack("addExpense");
                    ft.commit();

                }
            }
        });

        Button endDateBtn = view.findViewById(R.id.endDateButton);
        endDateBtn.setTextSize(screenWidth * 0.015f);
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(kDateString, date);
                bundle.putBoolean(kIsFilter, true);
                bundle.putString(kChooseOption, kEndDate);
                bundle.putString(kStartDate, startDate);
                bundle.putString(kEndDate, endDate);
                bundle.putString(kTag, tagSpinner.getSelectedItem().toString());

                DateSelectionFragment dateSelectionFragment = new DateSelectionFragment();
                dateSelectionFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragmentContainer, dateSelectionFragment);
                fragmentTransaction.addToBackStack("datePickerFragment");
                fragmentTransaction.commit();
            }
        });


        PrepareQuery();
        AddElementsToDisplay(queryToListen, displayLinear);

        return view;
    }

    private void PrepareQuery()
    {
        if ((!tag.isEmpty()) || (!startDate.isEmpty()) || (!endDate.isEmpty()))
        {
            Log.d("ERROR", "if reached");
            Long startDateInMillis=0l;
            Long endDateInMillis=0l;

            if (!startDate.isEmpty())
            {
                Integer dateDigit;
                Integer monthDigit;
                Integer yearDigit;

                String dateSplit[] = startDate.split("/");
                dateDigit = Integer.parseInt(dateSplit[0]);
                monthDigit = Integer.parseInt(dateSplit[1]);
                yearDigit = Integer.parseInt(dateSplit[2]);

                Calendar cali = Calendar.getInstance();
                cali.set(yearDigit, monthDigit-1, dateDigit, 0,0,0);
                startDateInMillis = cali.getTimeInMillis();
            }

            if (!endDate.isEmpty())
            {
                Integer dateDigit;
                Integer monthDigit;
                Integer yearDigit;

                String dateSplit[] = endDate.split("/");
                dateDigit = Integer.parseInt(dateSplit[0]);
                monthDigit = Integer.parseInt(dateSplit[1]);
                yearDigit = Integer.parseInt(dateSplit[2]);

                Calendar cali = Calendar.getInstance();
                cali.set(yearDigit, monthDigit-1, dateDigit, 0,0,0);
                endDateInMillis = cali.getTimeInMillis();

            }

            if ((!tag.isEmpty() || !tag.equals("Choose One")) && !startDate.isEmpty() && !endDate.isEmpty()) {
                Log.d("ERROR", "1...");
                checkTag = true;

                    queryToListen = myRef.orderByChild("date_tag").startAfter(startDateInMillis.toString() + "_" + tag).endBefore(endDateInMillis.toString() + "_" + tag);

                }
            else if(((!tag.isEmpty() || !tag.equals("Choose One")) && !startDate.isEmpty() && endDate.isEmpty()))
            {
                checkTag = true;
                Log.d("ERROR", "2...");

                    queryToListen = myRef.orderByChild("date_tag").startAfter(startDateInMillis.toString() + "_" + tag);

            }
            else if(((!tag.isEmpty() || !tag.equals("Choose One")) && startDate.isEmpty() && !endDate.isEmpty()))
            {
                checkTag = true;
                Log.d("ERROR", "3...");

                queryToListen = myRef.orderByChild("date_tag").endBefore(endDateInMillis.toString()+"_"+tag);
                }
            else if(((tag.isEmpty() || tag.equals("Choose One")) && !startDate.isEmpty() && !endDate.isEmpty()))
            {
                checkTag = false;
                Log.d("ERROR", "4...");


                    queryToListen = myRef.orderByChild("date").startAfter(startDateInMillis).endBefore(endDateInMillis);

            }
            else if((!tag.isEmpty() || !tag.equals("Choose One")) && startDate.isEmpty() && endDate.isEmpty())
            {
                checkTag = false;
                Log.d("ERROR", "5...");

                    queryToListen = myRef.orderByChild("tag").equalTo(tag);

            }
            else if((tag.isEmpty() || tag.equals("Choose One")) && !startDate.isEmpty() && endDate.isEmpty())
            {
                checkTag = false;
                Log.d("ERROR", "6...");

                queryToListen = myRef.orderByChild("date").startAfter(startDateInMillis);
            }
            else if((tag.isEmpty() || tag.equals("Choose One")) && startDate.isEmpty() && !endDate.isEmpty())
            {
                checkTag = false;
                Log.d("ERROR", "7...");

                queryToListen = myRef.orderByChild("date").endBefore(endDateInMillis);

            }
        }
        else {

                Log.d("SRS", "else case reached");
                queryToListen = myRef.orderByChild("date");

        }
    }

    private void AddElementsToDisplay(Query queryToListen, LinearLayout displayLinear)
    {
            isScrollDoneByUser = false;

            queryToListen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(linearParams);

                ScrollView scroll = new ScrollView(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                scroll.setLayoutParams(layoutParams);
                scroll.addView(linearLayout);

                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.scrollTo(0, ((numOfElements) * screenHeight/5));
                    }
                });

                numOfElements = 0;

                Double totalAmount = 0.0;
                Iterable<DataSnapshot> iter = snapshot.getChildren();
                if(snapshot.getChildrenCount() == 0){ isQueryHasNoChildren = true; return;}
                //queryToListen.removeEventListener(oldListener);
                Boolean isFirst = true;
                for (DataSnapshot obj : iter) {
                    ExpenseEvent post = obj.getValue(ExpenseEvent.class);

                    LayoutInflater inflater = getLayoutInflater();
                    View temp = inflater.inflate(R.layout.display_element, null);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(screenWidth, screenHeight / 5);
                    temp.setLayoutParams(param);
                    CardView card = new CardView(getContext());
                    card.setRadius(2);
                    card.setBackgroundResource(R.drawable.normal_display);

                    numOfElements = numOfElements + 1;

                    TextView dateText = temp.findViewById(R.id.dateTxt);
                    if (isFirst)
                    {
                        firstDateTime = post.getDate();
                        isFirst = false;
                    }
                    lastDateTime = post.getDate();
                    Log.d("SRS", post.getDate().toString());
                    dateText.setText(getDateFromMillis(post.getDate()));
                    TextView tagText = temp.findViewById(R.id.tagTxt);
                    tagText.setText(post.getTag());
                    TextView commentText = temp.findViewById(R.id.commentTxt);
                    commentText.setText(post.getComment());
                    TextView amountText = temp.findViewById(R.id.amountTxt);
                    amountText.setText(post.getAmount().toString());
                    totalAmount = totalAmount + post.getAmount();
                    card.addView(temp);
                    card.setClickable(true);
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectedItemToDelete.contains(obj.getKey()))
                            {
                                card.setBackgroundResource(R.drawable.normal_display);
                                selectedItemToDelete.remove(obj.getKey());
                            }
                            else {
                                card.setBackgroundResource(R.drawable.selected_display);
                                selectedItemToDelete.add(obj.getKey());
                            }
                        }
                    });
                    linearLayout.addView(card);
                }

                LinearLayout totalLayout = new LinearLayout(getContext());
                totalLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView totalText = new TextView(getContext());
                LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                p1.weight = 1f;
                totalText.setText("Total Amount:  ");
                totalText.setLayoutParams(p1);
                totalText.setTextColor(getResources().getColor(R.color.white));
                totalText.setTextSize(screenWidth * 0.02f);
                totalLayout.setBackground(getResources().getDrawable(R.drawable.sample_border));
                totalLayout.addView(totalText);

                View spaceView = new View(getContext());
                spaceView.setLayoutParams(p1);
                totalLayout.addView(spaceView);

                TextView totalAmt = new TextView(getContext());
                totalAmt.setText(totalAmount.toString());
                totalAmt.setLayoutParams(p1);
                totalAmt.setTextColor(getResources().getColor(R.color.white));
                totalAmt.setTextSize(screenWidth * 0.02f);
                totalLayout.addView(totalAmt);

                linearLayout.addView(totalLayout);

                displayLinear.addView(scroll);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });


        Log.d("SRS","Finally added new value listener");
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

    String getDateFromMillis(Long number)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(number);
        Log.d("ERROR","millis Num->"+number);
        String convertedVal = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
        Log.d("ERROR","converted Val->"+convertedVal);
        String splitDate[] = convertedVal.split("/");
        String date = splitDate[0]+" "+getMonthName(Integer.parseInt(splitDate[1]))+" "+splitDate[2];
        return date;
    }
}