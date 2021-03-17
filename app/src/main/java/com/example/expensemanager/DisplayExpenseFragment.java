package com.example.expensemanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class DisplayExpenseFragment extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users/ExpenseEvent");

    public DisplayExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_expense, container, false);
        ScrollView displayLinear = (ScrollView) view.findViewById(R.id.displayLinear);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterable<DataSnapshot> iter = snapshot.getChildren();

                for (DataSnapshot obj: iter)
                {
                    ExpenseEvent post = obj.getValue(ExpenseEvent.class);

//                    View temp = view.findViewById(R.id.cardElem);
//                    CardView card = new CardView(getContext());
//
//                    TextView dateText = temp.findViewById(R.id.dateTxt);
//                    dateText.setText(post.getDate());
//                    TextView tagText = temp.findViewById(R.id.tagTxt);
//                    tagText.setText(post.getTag());
//                    TextView commentText = temp.findViewById(R.id.commentTxt);
//                    commentText.setText(post.getComment());
//                    TextView amountText = temp.findViewById(R.id.amountTxt);
//                    amountText.setText(post.getAmount().toString());
//                    card.addView(temp);
//                    displayLinear.addView(card);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });


        return view;
    }
}