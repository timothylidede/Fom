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

public class TheirConnection extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    private TextView mConnectionsCount;
    private ProgressBar mProgressBar;
    private SearchView searchView;
    private CardView mZeroConnections;

    private TextView mTheirUsername;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private String uid;
    private FirebaseMethods mFirebaseMethods;

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
        setContentView(R.layout.activity_their_connection);

        mTheirUsername = (TextView) findViewById(R.id.their_username);

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

        FirebaseDatabase.getInstance().getReference("user_account_settings")
                .orderByChild("user_id").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String their_username = "" + ds.child("display_name").getValue();

                            mTheirUsername.setText(their_username + "'s");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        getConnections();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        mFirebaseMethods = new FirebaseMethods(TheirConnection.this);
        mConnectionsCount = (TextView) findViewById(R.id.my_connections_count);
        mProgressBar = (ProgressBar) findViewById(R.id.my_connections_progressBar);
        mZeroConnections = (CardView) findViewById(R.id.zero_connections);

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
                System.out.println("Button Clicked");

                finish();
            }

        });

        getViewUserConnectionsCount();
    }

    private void getConnections(){
        mAuth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //get current user_id
        final String mPot = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //get path of database
        DatabaseReference myRefPot = FirebaseDatabase.getInstance().getReference("connections").child(uid);

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
                adapterConnection = new AdapterConnection(TheirConnection.this, connectionsList);
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
                .child(uid);
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