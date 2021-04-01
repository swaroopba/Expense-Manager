package com.example.expensemanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignInActivity<Integer> extends AppCompatActivity {
    final private String kSignInName = "SignInName";
    final private String kSignInEmail = "SignInEmail";
    final private String kSignInId = "SignInID";
    final private String kSharedPrefName = "com.example.ExpenseManager";

    private int screenWidth;
    private int screenHeight;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            storeGoogleSignInDetails(account);
            launchEntranceActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        screenWidth = screenSize.x;
        screenHeight = screenSize.y;

        String signInMessage = "Sign in with your account.";
        TextView signIn = findViewById(R.id.titleName);
        signIn.setTextSize(screenWidth * 0.02f);
        signIn.setText(signInMessage);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton myGoogleSignIn = findViewById(R.id.googleButton);
        myGoogleSignIn.setSize(SignInButton.SIZE_WIDE);
        myGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void storeGoogleSignInDetails(GoogleSignInAccount account)
    {
        SharedPreferences sp = getSharedPreferences(kSharedPrefName, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(kSignInName, account.getDisplayName());
        edit.putString(kSignInEmail, account.getEmail());
        edit.putString(kSignInId, account.getId());
        edit.commit();
    }

    private void launchEntranceActivity()
    {
        Intent launchIntent = new Intent(this, EntranceActivity.class);
        startActivity(launchIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            storeGoogleSignInDetails(account);

            launchEntranceActivity();
        } catch (ApiException e) {

            Toast.makeText(this, "Error while Signing In "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}