package com.savala.fom;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import adapter.AdapterUsers;
import utils.FirebaseMethods;

public class ViewUser extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    private ImageView uPhotoOneUser;
    private ImageView uPhotoTwoUser;
    private ImageView uPhotoThreeUser;
    private TextView uDisplayNameProfile, uCountConnectionsProfile, uCountPacksProfile, uCountPacksFollowingProfile;
    private CardView uConnectionsProfile, uPacksProfile, uPacksFollowingProfile, uNotConnected;
    private Context uContext;
    private LinearLayout linear;

    private CardView mLoseCard;
    private ProgressBar mLoading;

    private String displayName, username, imageOne, imageTwo, imageThree, myUid, description, email;
    private String theirDisplayName, theirUsername, theirImageOne, theirImageTwo, theirImageThree, theirDescription;

    private FirebaseAuth uAuth;
    private FirebaseAuth.AuthStateListener uAuthListener;
    private FirebaseDatabase uFirebaseDatabase;
    private DatabaseReference uRef, theirRef, theirPotentialRef;
    private FirebaseMethods mFirebaseMethods;

    private Button uConnectUserButton, uPendingUserButton, uConnectedUserButton, mLoseButton, uEditProfileButton ,uCreatePackButton, mViewCancelButton, uAcceptButton, uDeclineButton;

    private ProgressBar uLoadingHome;
    private String uid;
    AdapterUsers adapterUsers;

    private String currentState = "nothing";

    private int mConnCount = 0, mPotCount = 0, mPendCount = 0;
    private int mPacksFollowingCount = 0;
    private int mMyPacksCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view_user);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();


        linear = (LinearLayout) findViewById(R.id.linear);
        uDisplayNameProfile = (TextView) findViewById(R.id.display_name_row_user);
        uConnectionsProfile = (CardView) findViewById(R.id.connections_row_user);
        uCountConnectionsProfile = (TextView) findViewById(R.id.count_connections_view_user);
        uCountPacksProfile = (TextView) findViewById(R.id.count_packs_row_user);
        uPacksProfile = (CardView) findViewById(R.id.packs_row_user);
        uContext = ViewUser.this;
        mFirebaseMethods = new FirebaseMethods(this);
        uConnectUserButton = (Button) findViewById(R.id.connect_row_user_button);
        uPendingUserButton = (Button) findViewById(R.id.pending_row_user_button);
        uConnectedUserButton = (Button) findViewById(R.id.connected_row_user_button);
        uLoadingHome = (ProgressBar) findViewById(R.id.loading_row_user);
        uPhotoOneUser = (ImageView) findViewById(R.id.row_user_imageView1);
        uPhotoTwoUser = (ImageView) findViewById(R.id.row_user_imageView2);
        uPhotoThreeUser = (ImageView) findViewById(R.id.row_user_imageView3);
        uNotConnected = (CardView) findViewById(R.id.card_not_connected);
        uAcceptButton = (Button) findViewById(R.id.accept_row_user_button);
        uDeclineButton = (Button) findViewById(R.id.decline_row_user_button);
        mLoading = (ProgressBar) findViewById(R.id.loading_row_user);
        mLoading.setVisibility(View.VISIBLE);


        mLoseCard = (CardView) findViewById(R.id.lose_card);
        mLoseButton = (Button) findViewById(R.id.lose_button);
        mViewCancelButton = (Button) findViewById(R.id.view_cancel_button);


        uAuth = FirebaseAuth.getInstance();
        uFirebaseDatabase = FirebaseDatabase.getInstance();
        uRef = uFirebaseDatabase.getReference();


        //get uid of clicked user
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkStatus(uid);
                getViewUserConnectionsCount();
                getViewUserTheirPacksCount();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        uConnectionsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUser.this, TheirConnection.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        uPacksProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUser.this, TheirPacks.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });


        uConnectUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSendingRequest(uid);
            }
        });

        uPendingUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionRemovePending(uid);
            }
        });

        uConnectedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoseCard.setVisibility(View.VISIBLE);

                mLoseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actionRemoveFriend(uid);
                        mLoseCard.setVisibility(View.INVISIBLE);
                    }
                });

                mViewCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLoseCard.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        uAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAcceptRequest(uid);
            }
        });

        uDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDeclineRequest(uid);
            }
        });

        checkUserStatus();

        //get info of current user
        uRef = FirebaseDatabase.getInstance().getReference("user_account_settings");
        Query dbQuery = uRef.orderByChild("user_id").equalTo(myUid);
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

        //get info of the other user
        theirRef = FirebaseDatabase.getInstance().getReference("user_account_settings");
        Query theirQuery = theirRef.orderByChild("user_id").equalTo(uid);
        theirQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    theirDisplayName = "" + ds.child("display_name").getValue();
                    theirImageOne = "" + ds.child("image_one").getValue();
                    theirImageTwo = "" + ds.child("image_two").getValue();
                    theirImageThree = "" + ds.child("image_three").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_uid))
                .equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    String displayName = "" + ds.child("display_name").getValue();
                    String imageOne = "" + ds.child("image_one").getValue();
                    String imageTwo = "" + ds.child("image_two").getValue();
                    String imageThree = "" + ds.child("image_three").getValue();
                    String connections = "" + ds.child("connections_count").getValue();
                    String packs = "" + ds.child("packs_count").getValue();
                    String potentials =  "" + ds.child("potentials_count").getValue();

                    //set data
                    uDisplayNameProfile.setText(displayName);
                    uCountConnectionsProfile.setText(connections);
                    uCountPacksProfile.setText(packs);

                    try{
                        Picasso.get().load(imageOne)
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(uPhotoOneUser);
                    }catch(Exception e){}

                    try{
                        Picasso.get().load(imageTwo)
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(uPhotoTwoUser);
                    }catch(Exception e){}

                    try{
                        Picasso.get().load(imageThree)
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(uPhotoThreeUser);
                    }catch(Exception e){}
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkStatus(uid);
        getViewUserConnectionsCount();
        getViewUserTheirPacksCount();
    }

    private void checkStatus(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child("connections")
                .child(uid)
                .orderByChild("user_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            currentState = "connected";
                            setConnected();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("connections")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("user_id").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            currentState = "connected";
                            setConnected();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("potentials")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("user_id")
                .equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentState = "them_pending";
                    setAnswer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference()
                .child("potentials")
                .child(uid)
                .orderByChild("user_id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentState = "me_pending";
                    setPending();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(currentState.equals("nothing")){
            setConnect();
        }
    }


    private void setConnected(){
        if(currentState.equals("connected")) {
            uConnectUserButton.setVisibility(View.GONE);
            uPendingUserButton.setVisibility(View.GONE);
            uConnectedUserButton.setVisibility(View.VISIBLE);
            uAcceptButton.setVisibility(View.GONE);
            uDeclineButton.setVisibility(View.GONE);
            linear.setVisibility(View.VISIBLE);


            uNotConnected.setVisibility(View.GONE);
            uConnectionsProfile.setVisibility(View.VISIBLE);
            uPacksProfile.setVisibility(View.VISIBLE);
        }
    }

    private void setAnswer(){
        if(currentState.equals("them_pending")) {
            uConnectUserButton.setVisibility(View.GONE);
            uPendingUserButton.setVisibility(View.GONE);
            uConnectedUserButton.setVisibility(View.GONE);
            uAcceptButton.setVisibility(View.VISIBLE);
            uDeclineButton.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);


            uNotConnected.setVisibility(View.VISIBLE);
            uConnectionsProfile.setVisibility(View.GONE);
            uPacksProfile.setVisibility(View.GONE);
        }
    }

    private void setPending(){
        if(currentState.equals("me_pending")) {
            uConnectUserButton.setVisibility(View.GONE);
            uPendingUserButton.setVisibility(View.VISIBLE);
            uConnectedUserButton.setVisibility(View.GONE);
            uAcceptButton.setVisibility(View.GONE);
            uDeclineButton.setVisibility(View.GONE);
            linear.setVisibility(View.GONE);

            uNotConnected.setVisibility(View.VISIBLE);
            uConnectionsProfile.setVisibility(View.GONE);
            uPacksProfile.setVisibility(View.GONE);
        }
    }

    private void setConnect(){
        if(currentState.equals("nothing")){
            uConnectUserButton.setVisibility(View.VISIBLE);
            uPendingUserButton.setVisibility(View.GONE);
            uConnectedUserButton.setVisibility(View.GONE);
            uAcceptButton.setVisibility(View.GONE);
            uDeclineButton.setVisibility(View.GONE);
            linear.setVisibility(View.GONE);

            uNotConnected.setVisibility(View.VISIBLE);
            uConnectionsProfile.setVisibility(View.GONE);
            uPacksProfile.setVisibility(View.GONE);
        }
    }

    private void actionSendingRequest(String uid){
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseDatabase.getInstance().getReference()
                .child("potentials")
                .child(uid)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            currentState = "me_pending";
                            setPending();
                        }
                    }
                });
    }
    private void actionRemovePending(String uid){

        FirebaseDatabase.getInstance().getReference()
                .child("potentials")
                .child(uid)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    currentState = "nothing";
                    setConnect();
                }
            }
        });
    }

    private void actionAcceptRequest(String uid){
        HashMap<Object, String> theirHashMap = new HashMap<>();
        theirHashMap.put("user_id", uid);

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());


        FirebaseDatabase.getInstance().getReference()
                .child("potentials")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(uid)
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child("connections")
                .child(uid)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            currentState = "connected";
                            setConnected();
                            acceptRequestNotification("connected with you");
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("connections")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(uid)
                .setValue(theirHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    currentState = "connected";
                    setConnected();
                }
            }
        });
    }

    private void acceptRequestNotification(String notification) {
        String timestamp =  "" + System.currentTimeMillis();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("timestamp", timestamp);
        hashMap.put("sender_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("notification", notification);

        FirebaseDatabase.getInstance().getReference("notifications_user").child(uid)
                .child(timestamp).setValue(hashMap);
    }

    private void actionRemoveFriend(String uid){

        FirebaseDatabase.getInstance().getReference()
                .child("connections")
                .child(uid)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child("connections")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(uid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            currentState = "nothing";
                            setConnect();
                        }
                    }
                });
    }

    private void actionDeclineRequest(String uid){
            FirebaseDatabase.getInstance().getReference()
                    .child("potentials")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(uid)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        currentState = "nothing";
                        setConnect();
                    }
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
                uCountConnectionsProfile.setText(String.valueOf(mConnCount));

                mConnCount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getViewUserTheirPacksCount(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("my_packs")
                .child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    mMyPacksCount++;
                }
                uCountPacksProfile.setText(String.valueOf(mMyPacksCount));

                mMyPacksCount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkUserStatus(){
        //get current user
        FirebaseUser user = uAuth.getCurrentUser();

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
            uAuth.removeAuthStateListener(uAuthListener);
        }
    }
}