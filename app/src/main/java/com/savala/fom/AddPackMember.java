package com.savala.fom;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.AdapterAddMember;
import models.ModelAddMembers;

public class AddPackMember extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    private RecyclerView mAddUsers;
    private ImageView mBack;
    private SearchView searchView;

    private FirebaseAuth firebaseAuth;
    private String packId;
    private String myPackRole;

    private TextView mTitle;
    private FirebaseAuth.AuthStateListener uAuthListener;
    private String email, myUid;

    private ArrayList<ModelAddMembers> userList;
    private AdapterAddMember adapterAddMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_pack_member);

        mAddUsers = (RecyclerView) findViewById(R.id.add_users);
        mAddUsers.setNestedScrollingEnabled(false);

        //set its properties
        mAddUsers.setHasFixedSize(true);
        mAddUsers.setLayoutManager(new LinearLayoutManager(AddPackMember.this));
        mTitle = (TextView) findViewById(R.id.title);

        //init userList
        userList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        packId = getIntent().getStringExtra("packId");
        loadGroupInfo();

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGroupInfo();
                getConnections();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadGroupInfo() {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("pack_members");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("packs");
        ref.orderByChild("pack_id").equalTo(packId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String packId = ""+ ds.child("pack_id").getValue();
                    String packName = ""+ds.child("pack_name").getValue();
                    String packDescription = "" + ds.child("pack_description").getValue();
                    String alpha = "" + ds.child("alpha").getValue();
                    String timestamp = "" + ds.child("timestamp").getValue();

                    ref1.child(packId).child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        myPackRole = "" + snapshot.child("role").getValue();
                                        //mTitle.setText(packName + "("+myPackRole+")");

                                        getConnections();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getConnections(){
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();;
        //get path of database
        DatabaseReference myRefPot = FirebaseDatabase.getInstance().getReference("connections")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRefPot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelAddMembers userAccountSettings = ds.getValue(ModelAddMembers.class);

                    //get all users except currently signed in user
                    if(!userAccountSettings.getUser_id().equals(mUser.getUid())){
                        userList.add(userAccountSettings);
                    }
                }

                //adapter
                adapterAddMember = new AdapterAddMember(AddPackMember.this, userList, ""+packId, ""+myPackRole);
                //set to recyclerView
                mAddUsers.setAdapter(adapterAddMember);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!= null){
            email = user.getEmail();
            myUid = user.getUid();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(uAuthListener != null){
            firebaseAuth.removeAuthStateListener(uAuthListener);
        }
    }
}