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

import adapter.AdapterPackMembers;
import models.ModelPackMembers;
import utils.FirebaseMethods;

public class PackMembers extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    private TextView mPackMembersCount;
    private ProgressBar mProgressBar;
    private SearchView searchView;
    private CardView mZeroPackMembers;

    private TextView mPackName;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private String uid;
    private FirebaseMethods mFirebaseMethods;

    private String packId;

    RecyclerView recyclerView;
    AdapterPackMembers adapterPackMembers;
    ArrayList<ModelPackMembers> packMembersList;

    private int mPackMembers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pack_members);

        mPackName = (TextView) findViewById(R.id.pack_name);

        //init recyclerview
        recyclerView = findViewById(R.id.pack_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);

        mZeroPackMembers = findViewById(R.id.zero_members);

        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //init userList
        packMembersList = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMembers();
                getPackMembersCount();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //get uid of pack
        Intent intent = getIntent();
        packId = intent.getStringExtra("packId");

        FirebaseDatabase.getInstance().getReference("packs")
                .orderByChild("pack_id").equalTo(packId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String pack_name = "" + ds.child("pack_name").getValue();

                            mPackName.setText(pack_name + "'s");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        getMembers();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        mFirebaseMethods = new FirebaseMethods(PackMembers.this);
        mPackMembersCount = (TextView) findViewById(R.id.pack_members_count);
        mProgressBar = (ProgressBar) findViewById(R.id.pack_members_progressBar);


        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                finish();
            }

        });

        getPackMembersCount();
    }

    private void getMembers(){
        mAuth = FirebaseAuth.getInstance();
        //get path of database
        DatabaseReference myRefPot = FirebaseDatabase.getInstance().getReference("pack_members").child(packId);

        myRefPot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packMembersList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelPackMembers modelPackMembers = ds.getValue(ModelPackMembers.class);

                    packMembersList.add(modelPackMembers);

                }

                //adapter
                adapterPackMembers = new AdapterPackMembers(PackMembers.this, packMembersList);
                //set to recyclerView
                recyclerView.setAdapter(adapterPackMembers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPackMembersCount(){
        mPackMembers = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("pack_members")
                .child(packId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    mPackMembers++;
                }
                mPackMembersCount.setText(String.valueOf(mPackMembers));

                int conn = Integer.parseInt(mPackMembersCount.getText().toString());

                if(conn == 0){
                    mZeroPackMembers.setVisibility(View.VISIBLE);
                }else{
                    mZeroPackMembers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}