package com.savala.fom;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import utils.FirebaseMethods;

import static android.content.ContentValues.TAG;

public class ViewMyself extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;
    private LinearLayout mLinear;

    private ImageView photoOne;
    private ImageView photoTwo;
    private ImageView photoThree;
    private TextView mUsernameProfile, mDisplayNameProfile, mCountConnectionsProfile, mCountPacksProfile, mHomeDescription;
    private CardView mConnectionsProfile, mPacksProfile, mPacksFollowingProfile;
    private Context mContext;

    private ConstraintLayout mLayoutHomeFragment;
    private DatabaseReference uRef;
    private String displayName, username, imageOne, imageTwo, imageThree, myUid, description, email;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private Button mEditProfileButton, mCreatePackButton;

    private ProgressBar mLoadingHome;


    private int mConnCount = 0, mPendCount = 0, mPotCount = 0;
    private int mPacksFollowingCount = 0;
    private int mMyPacksCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view_myself);
        mDisplayNameProfile = (TextView) findViewById(R.id.display_name_profile);
        mConnectionsProfile = (CardView) findViewById(R.id.connections_profile);
        mCountConnectionsProfile = (TextView) findViewById(R.id.count_connections_profile);
        mCountPacksProfile = (TextView) findViewById(R.id.count_packs_profile);
        mPacksProfile = (CardView) findViewById(R.id.packs_profile);
        mContext = ViewMyself.this;
        mFirebaseMethods = new FirebaseMethods(mContext);
        mEditProfileButton = (Button) findViewById(R.id.edit_profile_button);
        mCreatePackButton = (Button) findViewById(R.id.home_create_pack_button);
        mLoadingHome = (ProgressBar) findViewById(R.id.loading_home);
        mLinear = (LinearLayout) findViewById(R.id.linear);

        mAuth = FirebaseAuth.getInstance();
        uRef = FirebaseDatabase.getInstance().getReference();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHomeConnectionsCount();
                getHomeMyPacksCount();

                swipeRefreshLayout.setRefreshing(false);
            }
        });


        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_home_fragment);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        mCreatePackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreatePack.class);
                startActivity(intent);
            }
        });

        mConnectionsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyConnections.class);
                startActivity(intent);
            }
        });

        mPacksProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyPacks.class);
                startActivity(intent);
            }
        });

        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        photoOne = (ImageView) findViewById(R.id.imageView1);
        photoTwo = (ImageView) findViewById(R.id.imageView2);
        photoThree = (ImageView) findViewById(R.id.imageView3);

        setupFirebaseAuth();

        getHomeConnectionsCount();
        getHomeMyPacksCount();
    }

    private void setProfileWidgets(UserSettings userSettings){
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        try{
            Picasso.get().load(settings.getImage_one())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(photoOne);
        }catch(Exception e){}

        try{
            Picasso.get().load(settings.getImage_two())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(photoTwo);
        }catch(Exception e){}

        try{
            Picasso.get().load(settings.getImage_three())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(photoThree);
        }catch(Exception e){}
        mDisplayNameProfile.setText(settings.getDisplay_name());
    }

    private void getHomeConnectionsCount(){
        mConnCount = 0;

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
                mCountConnectionsProfile.setText(String.valueOf(mConnCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getHomeMyPacksCount(){
        mMyPacksCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("my_packs")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    mMyPacksCount++;
                }
                mCountPacksProfile.setText(String.valueOf(mMyPacksCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkCurrentUser(){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(mContext, Sign.class);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mLoadingHome.setVisibility(View.VISIBLE);
        mLinear.setVisibility(View.INVISIBLE);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(snapshot));
                mLoadingHome.setVisibility(View.INVISIBLE);
                mLinear.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        checkCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
