package com.savala.fom;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CreatePack extends AppCompatActivity {

    private FirebaseAuth firebaseAuth, mAuth;

    private DatabaseReference uRef;
    private ImageView mClose;
    private long timeend;
    private String day;
    private CardView mCreatePackCard, mLifespanCard;
    private EditText mCreatePackName, mLifeSpan;
    private Button mCreatePackCancelButton, mContinueCreatePackButton, mSelectLifeSpan;
    private Button mTwelveHours, mOneDay, mThreeDays, mSevenDays;
    private ProgressDialog progressDialog;

    private String twelve_hours, one_day, three_days, seven_days;

    private String displayName, username, imageOne, imageTwo, imageThree, myUid, description, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_pack);

        firebaseAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        uRef = FirebaseDatabase.getInstance().getReference();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        mCreatePackCard = (CardView) findViewById(R.id.create_pack_card);
        mCreatePackName = (EditText) findViewById(R.id.create_pack_name);
        mCreatePackCancelButton = (Button) findViewById(R.id.create_packs_cancel_button);
        mContinueCreatePackButton = (Button) findViewById(R.id.continue_create_pack_button);
        mLifeSpan = (EditText) findViewById(R.id.lifespan);
        disableEditText(mLifeSpan);
        mClose = (ImageView) findViewById(R.id.close);
        mLifespanCard = (CardView) findViewById(R.id.lifespan_card);
        mSelectLifeSpan = (Button) findViewById(R.id.select_lifespan);
        mTwelveHours = (Button) findViewById(R.id.twelve_hour_button);
        mOneDay = (Button) findViewById(R.id.one_day_button);
        mThreeDays = (Button) findViewById(R.id.three_day_button);
        mSevenDays = (Button) findViewById(R.id.seven_day_button);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLifespanCard.setVisibility(View.INVISIBLE);
                mCreatePackCard.setVisibility(View.VISIBLE);
            }
        });

        mSelectLifeSpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreatePackCard.setVisibility(View.INVISIBLE);
                mLifespanCard.setVisibility(View.VISIBLE);
            }
        });

        mTwelveHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLifeSpan.setText("12 Hours", TextView.BufferType.EDITABLE);
                mLifespanCard.setVisibility(View.INVISIBLE);
                mCreatePackCard.setVisibility(View.VISIBLE);
            }
        });

        mOneDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLifeSpan.setText("One Day", TextView.BufferType.EDITABLE);
                mLifespanCard.setVisibility(View.INVISIBLE);
                mCreatePackCard.setVisibility(View.VISIBLE);
            }
        });

        mThreeDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLifeSpan.setText("Three Days", TextView.BufferType.EDITABLE);
                mLifespanCard.setVisibility(View.INVISIBLE);
                mCreatePackCard.setVisibility(View.VISIBLE);
            }
        });

        mSevenDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLifeSpan.setText("Seven Days", TextView.BufferType.EDITABLE);
                mLifespanCard.setVisibility(View.INVISIBLE);
                mCreatePackCard.setVisibility(View.VISIBLE);
            }
        });

        checkUser();

        //get info of current user
        uRef = FirebaseDatabase.getInstance().getReference("user_account_settings");
        Query dbQuery = uRef.orderByChild("user_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    displayName = "" + ds.child("display_name").getValue();
                    imageOne = "" + ds.child("image_one").getValue();
                    imageTwo = "" + ds.child("image_two").getValue();
                    imageThree = "" + ds.child("image_three").getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mCreatePackCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mContinueCreatePackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(CreatePack.this);
                progressDialog.setMessage("Creating Pack");

                //input title, description
                String packName = mCreatePackName.getText().toString().toUpperCase().trim();
                String mTimeEnd = mLifeSpan.getText().toString().trim();
                String p_timestamp = "" + System.currentTimeMillis();
                //validation
                if(TextUtils.isEmpty(packName)){
                    Toast.makeText(CreatePack.this, "Provide a name for your pack", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(mTimeEnd)){
                    Toast.makeText(CreatePack.this, "Provide the desired lifespan for your pack", Toast.LENGTH_SHORT).show();
                }else{

                    if(mTimeEnd.equals("12 Hours")){
                        timeend = System.currentTimeMillis() + 43200000;
                        day = "twelve_hours";
                    }
                    if(mTimeEnd.equals("One Day")){
                        timeend = System.currentTimeMillis() + 86400000;
                        day = "one_day";
                    }
                    if(mTimeEnd.equals("Three Days")){
                        timeend = System.currentTimeMillis() + 259200000;
                        day = "three_days";
                    }
                    if(mTimeEnd.equals("Seven Days")){
                        timeend = System.currentTimeMillis() + 604800000;
                        day = "seven_days";
                    }

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("pack_id", "" +p_timestamp);
                    hashMap.put("pack_name", ""+packName);
                    hashMap.put("timestamp", ""+p_timestamp);
                    hashMap.put("timeend", timeend);
                    hashMap.put("day", day);
                    hashMap.put("timestart", System.currentTimeMillis());
                    hashMap.put("alpha", ""+FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("members_count", 0);

                    DatabaseReference packRef = FirebaseDatabase.getInstance().getReference("packs");
                    packRef.child(p_timestamp)
                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //created successfully
                            mContinueCreatePackButton.setEnabled(false);
                            //setup member inf (add current user in group's participants list)
                            HashMap<String, String> secondHashMap = new HashMap<>();
                            secondHashMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            secondHashMap.put("role", "alpha");
                            secondHashMap.put("pack_id", p_timestamp);
                            secondHashMap.put("timestamp", p_timestamp);

                            HashMap<String, Object> thirdHashMap = new HashMap<>();
                            thirdHashMap.put("pack_id", p_timestamp);
                            thirdHashMap.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            thirdHashMap.put("alpha", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            thirdHashMap.put("timestart", System.currentTimeMillis());
                            thirdHashMap.put("day", day);
                            thirdHashMap.put("timeend", timeend);

                            FirebaseDatabase.getInstance().getReference("my_packs")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(p_timestamp)
                                    .setValue(thirdHashMap);

                            FirebaseDatabase.getInstance().getReference("my_packs_members_count")
                                    .child(p_timestamp)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(thirdHashMap);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_members");
                            ref.child(p_timestamp)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(secondHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //participant added
                                    progressDialog.dismiss();
                                    finish();
                                    Toast.makeText(CreatePack.this, "Pack created successfully\nCheck your packs list to start adding your friends", Toast.LENGTH_LONG).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed to add participant
                                    progressDialog.dismiss();
                                    Toast.makeText(CreatePack.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed
                                    progressDialog.dismiss();
                                    Toast.makeText(CreatePack.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void checkUser(){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){

        }
    }
}