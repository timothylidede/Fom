package com.savala.fom.SignUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savala.fom.Login.Login;
import com.savala.fom.R;

import models.User;
import models.UserAccountSettings;
import utils.NumericKeyBoardTransformationMethod;

import static android.content.ContentValues.TAG;

public class EmailAddress extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private Context mContext = EmailAddress.this;

    private String append = "";

    private String first_name, second_name, year, password, email, userID, display_name;
    private long age, newYear;

    private EditText mFirstName, mSecondName, mYear, mEmail, mPassword;

    private Button signUpButton, proceedButton;
    private ProgressBar mProgressBar;
    private ImageView mShowPassword, mHidePassword;
    private CardView mCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_email_address);
        setupFirebaseAuth();


        try{
            init();
        }catch(NumberFormatException e){
            Toast.makeText(EmailAddress.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        mFirstName = (EditText) findViewById(R.id.text_first_name);
        mSecondName = (EditText) findViewById(R.id.text_second_name);
        mYear = (EditText) findViewById(R.id.text_year);
        mEmail = (EditText) findViewById(R.id.text_email);
        mPassword = (EditText) findViewById(R.id.text_password);

        mYear.setTransformationMethod(new NumericKeyBoardTransformationMethod());


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_signUp);
        mShowPassword = (ImageView) findViewById(R.id.showPassword);
        mHidePassword = (ImageView) findViewById(R.id.hidePassword);
        mCardView = (CardView) findViewById(R.id.button_card);


        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                finish();
            }

        });


        mHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHidePassword.setVisibility(View.INVISIBLE);
                mShowPassword.setVisibility(View.VISIBLE);
                mPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        mShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPassword.setTransformationMethod(null);
                mShowPassword.setVisibility(View.INVISIBLE);
                mHidePassword.setVisibility(View.VISIBLE);
            }
        });

    }


    private boolean isStringNull(String string){
        if(string.equals("")){
            return true;
        }else{
            return false;
        }
    }


    private void init(){
        proceedButton = (Button) findViewById(R.id.proceed_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to signup");
                first_name = mFirstName.getText().toString().trim();
                second_name = mSecondName.getText().toString().trim();
                String lowerFirstName = first_name.toLowerCase();
                String lowerSecondName = second_name.toLowerCase();
                display_name = first_name + " " + second_name;
                year = mYear.getText().toString();


                try{
                    newYear = Long.parseLong(year);
                }catch(NumberFormatException e){
                    Toast.makeText(EmailAddress.this, "Must fill in birth-year", Toast.LENGTH_SHORT).show();
                }

                password = mPassword.getText().toString().trim();
                email = mEmail.getText().toString().trim();

                age = 2001 - newYear;

                if(isStringNull(email)){
                    Toast.makeText(EmailAddress.this, "Provide email", Toast.LENGTH_SHORT).show();
                }else if(isStringNull(password)){
                    Toast.makeText(EmailAddress.this, "Provide password", Toast.LENGTH_SHORT).show();
                }else if(isStringNull(year)){
                    Toast.makeText(EmailAddress.this, "Provide birth-year", Toast.LENGTH_SHORT).show();
                }else if(isStringNull(first_name)){
                    Toast.makeText(EmailAddress.this, "Provide first name", Toast.LENGTH_SHORT).show();
                }else if (newYear > 2008) {
                    Toast.makeText(EmailAddress.this, "You are too young", Toast.LENGTH_SHORT).show();
                }else if (newYear < 1930) {
                    Toast.makeText(EmailAddress.this, "Invalid year", Toast.LENGTH_SHORT).show();
                }else if(isStringNull(second_name)){
                    Toast.makeText(EmailAddress.this, "Provide second name", Toast.LENGTH_SHORT).show();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(EmailAddress.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }else {

                    if(passIsValid(password)){
                        mProgressBar.setVisibility(View.VISIBLE);
                        signUpButton.setVisibility(View.INVISIBLE);
                        mCardView.setVisibility(View.INVISIBLE);
                        proceedButton.setVisibility(View.INVISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(EmailAddress.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "onComplete: " + task.isSuccessful());

                                        if (!task.isSuccessful()){
                                            Toast.makeText(EmailAddress.this
                                                    , "Enter valid email address"
                                                    , Toast.LENGTH_SHORT).show();
                                            mProgressBar.setVisibility(View.GONE);
                                            mCardView.setVisibility(View.VISIBLE);
                                            signUpButton.setVisibility(View.VISIBLE);
                                        }else if(task.isSuccessful()){

                                            userID = mAuth.getCurrentUser().getUid();

                                            User user = new User(age, email, userID, newYear);
                                            myRef.child(mContext.getString(R.string.dbname_user))
                                                    .child(userID)
                                                    .setValue(user);
                                            Toast.makeText(mContext, "Signup successful. Sending verification email", Toast.LENGTH_SHORT).show();

                                            //add new user_account_settings
                                            UserAccountSettings settings = new UserAccountSettings(0, display_name, "none", "none", "none", 0, 0, 0, userID);
                                            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                                                    .child(userID)
                                                    .setValue(settings);

                                            mCardView.setVisibility(View.VISIBLE);
                                            mAuth.getCurrentUser().sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                proceedButton.setVisibility(View.VISIBLE);
                                                                Toast.makeText(EmailAddress.this
                                                                        , "Successful. Check your mail inbox for verification"
                                                                        , Toast.LENGTH_LONG).show();

                                                                proceedButton.setOnClickListener(new View.OnClickListener() {
                                                                    public void onClick(View v) {
                                                                        signUpButton.setVisibility(View.INVISIBLE);

                                                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }

                                                                });
                                                            }else{
                                                                Toast.makeText(EmailAddress.this
                                                                        , "Please try again"
                                                                        , Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(EmailAddress.this, "Must be between 6 and 15 characters and avoid space", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    public static boolean passIsValid(String password)
    {

        // for checking if password length
        // is between 6 and 15
        if (!((password.length() >= 6)
                && (password.length() <= 15))) {
            return false;
        }

        // to check space
        if (password.contains(" ")) {
            return false;
        }

        // if all conditions fails
        return true;
    }


    /*FIREBASE*/
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}