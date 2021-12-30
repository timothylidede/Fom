package com.savala.fom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText mResetPassEmail;
    private Button mResetPassButton;
    private ProgressBar mProgressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset_password);

        mResetPassButton = findViewById(R.id.reset_pass_button);
        mResetPassEmail = findViewById(R.id.reset_pass_email);
        mProgressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                finish();
            }

        });

        mResetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        String email = mResetPassEmail.getText().toString().trim();

        if(email.isEmpty()){
            Toast.makeText(ResetPassword.this, "Provide email", Toast.LENGTH_SHORT).show();
            mResetPassEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(ResetPassword.this, "Provide a valid email", Toast.LENGTH_SHORT).show();
            mResetPassEmail.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPassword.this, "We have sent you an email to reset your password", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ResetPassword.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}