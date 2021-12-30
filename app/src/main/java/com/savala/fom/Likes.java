package com.savala.fom;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.AdapterLikes;
import models.ModelConnection;
import utils.FirebaseMethods;

public class Likes extends AppCompatActivity {
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
    private String postId;

    RecyclerView recyclerView;
    AdapterLikes adapterLikes;
    ArrayList<ModelConnection> connectionsList;

    private int mPendCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_likes);

        //init recyclerview
        recyclerView = findViewById(R.id.potentials_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);

        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //init userList
        connectionsList = new ArrayList<>();

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        getLikesCount();
        getLikes();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLikes();
                getLikesCount();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mFirebaseMethods = new FirebaseMethods(Likes.this);
        mPotentialsCount = (TextView) findViewById(R.id.likes_count);
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

    private void getLikes(){
        //get path of database
        DatabaseReference myRefPot = FirebaseDatabase.getInstance().getReference("likes").child(postId);

        myRefPot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                connectionsList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelConnection connection = ds.getValue(ModelConnection.class);

                    connectionsList.add(connection);
                }

                //adapter
                adapterLikes = new AdapterLikes(Likes.this, connectionsList);
                //set to recyclerView
                recyclerView.setAdapter(adapterLikes);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikesCount(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("likes")
                .child(postId);
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
