package com.savala.fom.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.savala.fom.R;

public class Username extends AppCompatActivity {

    public final static String EXTRA_URL = "Username.URL";

    public String getUsername() {
        return username;
    }

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    private EditText mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_username);

        mUsername = (EditText) findViewById(R.id.username);

        Button username_button = findViewById(R.id.username_button);
        username_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                username = mUsername.getText().toString();

                if(isStringNull(username)){
                    Toast.makeText(Username.this, "Fill in all fields provided", Toast.LENGTH_SHORT).show();
                }else {
                    Intent activity4Intent = new Intent(getApplicationContext(), Birthday.class);
                    startActivity(activity4Intent);
                    finish();
                }
            }

        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                finish();
            }

        });
    }

    public void startActivity(String url) {
        Intent intent = new Intent(this, EmailAddress.class);

        intent.putExtra(EXTRA_URL, url);
        startActivity(intent);
    }

    private boolean isStringNull(String string){
        if(string.equals("")){
            return true;
        }else{
            return false;
        }
    }
}