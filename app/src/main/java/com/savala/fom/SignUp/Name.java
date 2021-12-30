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


public class Name extends AppCompatActivity {

    public final static String EXTRA_URL = "Name.URL";

    private String first_name;
    private String second_name;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    private EditText mFirstName, mSecondName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_name);

        mFirstName = (EditText) findViewById(R.id.sign_up_first_name);
        mSecondName = (EditText) findViewById(R.id.sign_up_second_name);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                finish();
            }

        });

        Button nameButton = findViewById(R.id.name_button);
        nameButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                first_name = mFirstName.getText().toString();
                second_name = mSecondName.getText().toString();

                if(isStringNull(first_name)||isStringNull(second_name)){
                    Toast.makeText(Name.this, "Fill in all fields provided", Toast.LENGTH_SHORT).show();
                }else {
                    Intent activity4Intent = new Intent(getApplicationContext(), Username.class);
                    startActivity(activity4Intent);
                    finish();
                }
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