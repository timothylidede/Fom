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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.AdapterConnection;
import models.ModelConnection;
import utils.FirebaseMethods;

public class MyConnections extends AppCompatActivity {
    private TextView mConnectionsCount;
    private ProgressBar mProgressBar;
    private SearchView searchView;
    private CardView mZeroConnections;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private String uid;
    private FirebaseMethods mFirebaseMethods;

    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    RecyclerView recyclerView;
    AdapterConnection adapterConnection;
    ArrayList<ModelConnection> connectionsList;

    private int mConnCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_connections);

        //init recyclerview
        recyclerView = findViewById(R.id.connections_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);

        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //init userList
        connectionsList = new ArrayList<>();

        //get uid of clicked user
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        getConnections();
        getViewUserConnectionsCount();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        mFirebaseMethods = new FirebaseMethods(MyConnections.this);
        mConnectionsCount = (TextView) findViewById(R.id.my_connections_count);
        mProgressBar = (ProgressBar) findViewById(R.id.my_connections_progressBar);
        mZeroConnections = (CardView) findViewById(R.id.my_zero_connections);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getViewUserConnectionsCount();
                getConnections();

                swipeRefreshLayout.setRefreshing(false);
            }
        });


        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                finish();
            }

        });
    }

    private void getConnections(){
        mAuth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //get current user_id
        final String mPot = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //get path of database
        DatabaseReference myRefPot = FirebaseDatabase.getInstance().getReference("connections").child(mPot);

        myRefPot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                connectionsList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelConnection connections = ds.getValue(ModelConnection.class);

                    connectionsList.add(connections);

                }

                //adapter
                adapterConnection = new AdapterConnection(MyConnections.this, connectionsList);
                //set to recyclerView
                recyclerView.setAdapter(adapterConnection);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getViewUserConnectionsCount(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("connections")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    mConnCount++;
                }
                mConnectionsCount.setText(String.valueOf(mConnCount));

                mConnCount = 0;

                int conn = Integer.parseInt(mConnectionsCount.getText().toString());

                if(conn == 0){
                    mZeroConnections.setVisibility(View.VISIBLE);
                }else{
                    mZeroConnections.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}