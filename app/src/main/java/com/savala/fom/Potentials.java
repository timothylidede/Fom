package com.savala.fom;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.AdapterPotentials;
import models.ModelPotentials;
import utils.FirebaseMethods;

public class Potentials extends AppCompatActivity {
    private TextView mPotentialsCount;
    private ProgressBar mProgressBar;
    private SearchView searchView;
    private CardView mZeroPotentials;

    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private FirebaseMethods mFirebaseMethods;

    RecyclerView recyclerView;
    AdapterPotentials adapterPotentials;
    ArrayList<ModelPotentials> potentialsList;

    private int mPendCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_potentials);

        //init recyclerview
        recyclerView = findViewById(R.id.potentials_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);

        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //init userList
        potentialsList = new ArrayList<>();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        getViewUserPotentialsCount();
        getPotentials();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPotentials();
                getViewUserPotentialsCount();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mFirebaseMethods = new FirebaseMethods(Potentials.this);
        mPotentialsCount = (TextView) findViewById(R.id.my_potentials_count);
        mProgressBar = (ProgressBar) findViewById(R.id.my_potentials_progressBar);
        mZeroPotentials = (CardView) findViewById(R.id.zero_potentials);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                finish();
            }

        });
    }

    private void getPotentials(){
        mAuth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //get current user_id
        final String mPot = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //get path of database
        DatabaseReference myRefPot = FirebaseDatabase.getInstance().getReference("potentials").child(mPot);

        myRefPot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                potentialsList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelPotentials potentials = ds.getValue(ModelPotentials.class);

                    potentialsList.add(potentials);
                }

                //adapter
                adapterPotentials = new AdapterPotentials(Potentials.this, potentialsList);
                //set to recyclerView
                recyclerView.setAdapter(adapterPotentials);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getViewUserPotentialsCount(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("potentials")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    mPendCount++;
                }
                mPotentialsCount.setText(String.valueOf(mPendCount));

                mPendCount = 0;

                int pot = Integer.parseInt(mPotentialsCount.getText().toString());

                if(pot == 0){
                    mZeroPotentials.setVisibility(View.VISIBLE);
                }else{
                    mZeroPotentials.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}