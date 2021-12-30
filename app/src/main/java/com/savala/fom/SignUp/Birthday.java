package com.savala.fom.SignUp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.savala.fom.R;

import java.util.Calendar;

public class Birthday extends AppCompatActivity /*implements DatePickerDialog.OnDateSetListener*/ {

    public final static String EXTRA_URL = "Birthday.URL";

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_birthday);
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        Button birthday_button = findViewById(R.id.birthday_button);
        birthday_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                Intent activity4Intent = new Intent(getApplicationContext(), EmailAddress.class);
                startActivity(activity4Intent);
                finish();
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

    private String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int yr = cal.get(Calendar.YEAR);
        int year = yr - 18;

        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(dayOfMonth, month, year);
    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);
                dateButton.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int yr = cal.get(Calendar.YEAR);
        int year = yr - 18;

        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        long fifteen = 473300000000L + 86400000L;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()- fifteen);
    }

    private String makeDateString(int dayOfMonth, int month, int year){
        return dayOfMonth + " " + getMonthFormat(month) + " "  + year;
    }

    private String getMonthFormat(int month){
        if(month == 1)
            return "JANUARY";
        if(month == 2)
            return "FEBRUARY";
        if(month == 3)
            return "MARCH";
        if(month == 4)
            return "APRIL";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUNE";
        if(month == 7)
            return "JULY";
        if(month == 8)
            return "AUGUST";
        if(month == 9)
            return "SEPTEMBER";
        if(month == 10)
            return "OCTOBER";
        if(month == 11)
            return "NOVEMBER";
        if(month == 12)
            return "DECEMBER";

        return "JULY";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}