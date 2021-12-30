package com.savala.fom.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.savala.fom.Home;
import com.savala.fom.R;
import com.savala.fom.ResetPassword;

import static android.content.ContentValues.TAG;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context LoginContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mForgotPassword;

    private ImageView mShowPassword, mHidePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        LoginContext = Login.this;

        mProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        setupFirebaseAuth();
        init();

        mShowPassword = (ImageView) findViewById(R.id.login_show_password);
        mHidePassword = (ImageView) findViewById(R.id.login_hide_password);

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

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ResetPassword.class);
                startActivity(intent);
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

    /* Firebase */

    private void init(){
        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to login");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(isStringNull(email) || isStringNull(password)){
                    Toast.makeText(LoginContext, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    buttonLogin.setVisibility(View.INVISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "onComplete: " + task.isSuccessful());

                                    if (!task.isSuccessful()){
                                        Toast.makeText(Login.this, "Wrong login details", Toast.LENGTH_SHORT).show();
                                        buttonLogin.setVisibility(View.VISIBLE);
                                        mProgressBar.setVisibility(View.GONE);
                                    }else{
                                        if(mAuth.getCurrentUser().isEmailVerified()){
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Intent intent = new Intent(LoginContext, Home.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                            buttonLogin.setVisibility(View.VISIBLE);
                                            mProgressBar.setVisibility(View.GONE);
                                        }else{
                                            Toast.makeText(Login.this, "Verify your Email Address", Toast.LENGTH_LONG).show();
                                            buttonLogin.setVisibility(View.VISIBLE);
                                            mProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });

                }
            }

        });
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
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