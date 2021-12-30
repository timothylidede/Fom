package com.savala.fom;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.util.ArrayList;
import java.util.HashMap;

import adapter.AdapterFeedPosts;
import adapter.AdapterPackImages;
import models.ModelPackMembersImages;
import models.ModelPosts;

public class ViewPack extends AppCompatActivity {
    private String packId, postId, myPackRole = "nothing";
    private TextView mViewPackName, mViewPackCountMembers;
    private ImageView mViewPackChat, mViewPackAddLocation, mViewPackAddMembers;
    private ImageView mEditPackImageView;
    private RelativeLayout memberPrivilege;
    private CardView mNoPosts;

    private CardView mEditPackCard;
    private EditText mEditPackName;
    private Button mEditCancelButton, mEditContinueButton;

    private CardView mLeaveOrEditCard;
    private Button leavePack, editPack, nonePack;

    private CardView mViewPackCardMembers;
    private TextView mAlphaName;

    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    private int mConnCount = 0, mMemberCount = 0, mPostCount = 0;

    private String displayName, username, imageOne, imageTwo, imageThree, myUid, description, email;
    private long timeend, timestart;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener uAuthListener;

    RecyclerView packImagesRecyclerView;
    AdapterPackImages adapterPackImages;
    ArrayList<ModelPackMembersImages> packMembersImages;

    RecyclerView recyclerView;
    RecyclerView.RecyclerListener recyclerListener;
    AdapterFeedPosts adapterFeedPosts;
    ArrayList<ModelPosts> postsList;

    private ImageView mTimeRed, mTimeGreen, mTimeOrange;
    private TextView mDayRed, mDayGreen, mDayOrange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view_pack);

        firebaseAuth = FirebaseAuth.getInstance();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        RelativeLayout layout3 = (RelativeLayout) findViewById(R.id.layout3);
        AnimationDrawable animationDrawable3 = (AnimationDrawable) layout3.getBackground();
        animationDrawable3.setEnterFadeDuration(3000);
        animationDrawable3.setExitFadeDuration(3000);
        animationDrawable3.start();

        LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout2);
        AnimationDrawable animationDrawable2 = (AnimationDrawable) layout2.getBackground();
        animationDrawable2.setEnterFadeDuration(3000);
        animationDrawable2.setExitFadeDuration(3000);
        animationDrawable2.start();

        packImagesRecyclerView = findViewById(R.id.pack_images);
        packImagesRecyclerView.setNestedScrollingEnabled(false);
        memberPrivilege = (RelativeLayout) findViewById(R.id.member_privilege);

        packImagesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewPack.this, LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        packImagesRecyclerView.setLayoutManager(layoutManager);

        packMembersImages = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGroupInfo();
                loadMyGroupRole();
                checkStatus(packId);
                getAllMembers();
                getPosts();
                getViewPackMembersCount();

                swipeRefreshLayout.setRefreshing(false);
            }
        });


        //init recyclerview
        recyclerView = findViewById(R.id.feed_posts);
        recyclerView.setNestedScrollingEnabled(false);

        //set its properties
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(ViewPack.this, LinearLayoutManager.VERTICAL, false);
        layoutManager2.setReverseLayout(true);
        layoutManager2.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager2);

        //init userList
        postsList = new ArrayList<>();


        mNoPosts = (CardView) findViewById(R.id.card_no_posts);
        mViewPackName = findViewById(R.id.view_pack_name);
        mAlphaName = findViewById(R.id.display_name_alpha);
        mViewPackAddMembers = findViewById(R.id.view_pack_add_member);
        mViewPackCountMembers = findViewById(R.id.view_pack_count_members);
        mViewPackChat = findViewById(R.id.view_pack_chat);
        mViewPackAddLocation = findViewById(R.id.view_pack_add_location);
        mViewPackCardMembers = findViewById(R.id.view_packs_card_ze_members);

        mEditPackImageView = findViewById(R.id.edit_pack_image_view);
        mEditPackCard = findViewById(R.id.edit_pack_card);
        mEditPackName = findViewById(R.id.edit_pack_name);
        mEditCancelButton = findViewById(R.id.edit_pack_cancel_button);
        mEditContinueButton =findViewById(R.id.continue_edit_pack_button);

        mLeaveOrEditCard = findViewById(R.id.leave_or_edit_card);
        leavePack = findViewById(R.id.leave_pack_button);
        editPack = findViewById(R.id.edit_pack_button);
        nonePack = findViewById(R.id.none_pack_button);

        mLeaveOrEditCard.setVisibility(View.INVISIBLE);
        mEditPackCard.setVisibility(View.INVISIBLE);

        mTimeGreen = (ImageView) findViewById(R.id.time_green);
        mTimeRed = (ImageView) findViewById(R.id.time_red);
        mTimeOrange = (ImageView) findViewById(R.id.time_orange);
        mDayGreen = (TextView) findViewById(R.id.day_green);
        mDayRed = (TextView) findViewById(R.id.day_red);
        mDayOrange = (TextView) findViewById(R.id.day_orange);

        Intent intent = getIntent();
        packId = intent.getStringExtra("packId");

        loadGroupInfo();
        loadMyGroupRole();
        checkStatus(packId);
        getAllMembers();
        getPosts();
        getViewPackMembersCount();

        FirebaseDatabase.getInstance().getReference("packs")
                .orderByChild("pack_id").equalTo(packId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String newTimeend = "" + ds.child("timeend").getValue();
                            String newTimestart = "" + ds.child("timestart").getValue();
                            String day = "" + ds.child("day").getValue();

                            timeend = Long.parseLong(newTimeend);
                            timestart = Long.parseLong(newTimestart);
                            long timecurrent = System.currentTimeMillis();
                            long half_day = timestart + 43200000;
                            long one_day = timestart + 86400000;
                            long two_days = one_day + 86400000;
                            long two_and_a_half_days = two_days + 43200000;
                            long three_days = two_days + 86400000;
                            long four_days = three_days + 86400000;
                            long five_days = four_days + 86400000;
                            long six_days = five_days + 86400000;
                            long six_and_a_half_days = six_days + 43200000;
                            long seven_days = six_days + 86400000;

                            if(day.equals("twelve_hours")){
                                if(timecurrent > timestart && timecurrent < timeend){
                                    if(timecurrent < half_day){
                                        twelveHoursLeft();
                                    }
                                }
                            }
                            if(day.equals("one_day")){
                                if(timecurrent > timestart && timecurrent < timeend){
                                    if(timecurrent < one_day){
                                        oneDayLeft();
                                    }else if(timecurrent < half_day){
                                        twelveHoursLeft();
                                    }
                                }
                            }
                            if(day.equals("three_days")){
                                if(timecurrent > timestart && timecurrent < timeend){
                                    if(timecurrent < one_day){
                                        threeDaysLeft();
                                    }else if(timecurrent > one_day && timecurrent < two_days){
                                        twoDaysLeft();
                                    }else if(timecurrent > two_days && timecurrent < two_and_a_half_days){
                                        oneDayLeft();
                                    }else if(timecurrent > two_and_a_half_days && timecurrent < three_days){
                                        twelveHoursLeft();
                                    }
                                }
                            }
                            if(day.equals("seven_days")){
                                if(timecurrent > timestart && timecurrent < timeend){
                                    if(timecurrent < one_day){
                                        sevenDaysLeft();
                                    }else if(timecurrent > one_day && timecurrent < two_days){
                                        sixDaysLeft();
                                    }else if(timecurrent > two_days && timecurrent < three_days){
                                        fiveDaysLeft();
                                    }else if(timecurrent > three_days && timecurrent < four_days){
                                        fourDaysLeft();
                                    }else if(timecurrent > four_days && timecurrent < five_days){
                                        threeDaysLeft();
                                    }else if(timecurrent > five_days && timecurrent < six_days){
                                        twoDaysLeft();
                                    }else if(timecurrent > six_days && timecurrent < six_and_a_half_days){
                                        oneDayLeft();
                                    }else if(timecurrent > six_and_a_half_days && timecurrent < seven_days){
                                        twelveHoursLeft();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("packs").child(packId).child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            mNoPosts.setVisibility(View.GONE);
                        }else{
                            mNoPosts.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        mViewPackCardMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPack.this, PackMembers.class);
                intent.putExtra("packId", packId);
                startActivity(intent);
            }
        });

        mViewPackAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPack.this, PostLocation.class);
                intent.putExtra("packId", packId);
                startActivity(intent);
            }
        });

        mEditPackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeaveOrEditCard.setVisibility(View.VISIBLE);
            }
        });

        editPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPackCard.setVisibility(View.VISIBLE);
            }
        });

        nonePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeaveOrEditCard.setVisibility(View.INVISIBLE);
            }
        });

        mEditCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPackCard.setVisibility(View.GONE);
                mLeaveOrEditCard.setVisibility(View.GONE);
            }
        });

        mEditContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //input title, description
                String packName = mEditPackName.getText().toString().toUpperCase().trim();
                if(TextUtils.isEmpty(packName)){
                    Toast.makeText(ViewPack.this, "Provide a name for your pack", Toast.LENGTH_SHORT).show();
                }else{
                    editPack(
                         "" + packName
                    );
                }
            }
        });

        leavePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialogTitle = "";
                String dialogDescription = "";
                String positiveButtonTitle = "";

                dialogTitle = "Leave Pack?";
                dialogDescription = "Are you sure you want to leave the pack permanently?";
                positiveButtonTitle = "LEAVE";

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPack.this);
                builder.setTitle(dialogTitle)
                        .setMessage(dialogDescription)
                        .setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                leavePack();

                                FirebaseDatabase.getInstance().getReference("pack_members").child(packId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(!snapshot.exists()){
                                                    FirebaseDatabase.getInstance().getReference("pack_members")
                                                            .child(packId).removeValue();

                                                    FirebaseDatabase.getInstance().getReference("packs")
                                                            .child(packId).removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                finish();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });


        //get info of current user
        DatabaseReference uRef = FirebaseDatabase.getInstance().getReference("user_account_settings");
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

        mViewPackAddMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPack.this, AddPackMember.class);
                intent.putExtra("packId", packId);
                startActivity(intent);
            }
        });


        mViewPackChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPack.this, PackChat.class);
                intent.putExtra("packId", packId);
                startActivity(intent);
            }
        });
    }

    private void sevenDaysLeft(){
        mTimeGreen.setVisibility(View.VISIBLE);
        mTimeOrange.setVisibility(View.INVISIBLE);
        mTimeRed.setVisibility(View.INVISIBLE);

        mDayGreen.setVisibility(View.VISIBLE);
        mDayOrange.setVisibility(View.INVISIBLE);
        mDayRed.setVisibility(View.INVISIBLE);

        mDayGreen.setText("7 DAYS LEFT");
    }

    private void sixDaysLeft(){
        mTimeGreen.setVisibility(View.VISIBLE);
        mTimeOrange.setVisibility(View.INVISIBLE);
        mTimeRed.setVisibility(View.INVISIBLE);

        mDayGreen.setVisibility(View.VISIBLE);
        mDayOrange.setVisibility(View.INVISIBLE);
        mDayRed.setVisibility(View.INVISIBLE);

        mDayGreen.setText("6 DAYS LEFT");
    }

    private void fiveDaysLeft(){
        mTimeGreen.setVisibility(View.VISIBLE);
        mTimeOrange.setVisibility(View.INVISIBLE);
        mTimeRed.setVisibility(View.INVISIBLE);

        mDayGreen.setVisibility(View.VISIBLE);
        mDayOrange.setVisibility(View.INVISIBLE);
        mDayRed.setVisibility(View.INVISIBLE);

        mDayGreen.setText("5 DAYS LEFT");
    }

    private void fourDaysLeft(){
        mTimeGreen.setVisibility(View.INVISIBLE);
        mTimeOrange.setVisibility(View.VISIBLE);
        mTimeRed.setVisibility(View.INVISIBLE);

        mDayGreen.setVisibility(View.INVISIBLE);
        mDayOrange.setVisibility(View.VISIBLE);
        mDayRed.setVisibility(View.INVISIBLE);

        mDayOrange.setText("4 DAYS LEFT");
    }

    private void threeDaysLeft(){
        mTimeGreen.setVisibility(View.INVISIBLE);
        mTimeOrange.setVisibility(View.VISIBLE);
        mTimeRed.setVisibility(View.INVISIBLE);

        mDayGreen.setVisibility(View.INVISIBLE);
        mDayOrange.setVisibility(View.VISIBLE);
        mDayRed.setVisibility(View.INVISIBLE);

        mDayOrange.setText("3 DAYS LEFT");
    }

    private void twoDaysLeft(){
        mTimeGreen.setVisibility(View.INVISIBLE);
        mTimeOrange.setVisibility(View.INVISIBLE);
        mTimeRed.setVisibility(View.VISIBLE);

        mDayGreen.setVisibility(View.INVISIBLE);
        mDayOrange.setVisibility(View.INVISIBLE);
        mDayRed.setVisibility(View.VISIBLE);

        mDayRed.setText("2 DAYS LEFT");
    }

    private void oneDayLeft(){
        mTimeGreen.setVisibility(View.INVISIBLE);
        mTimeOrange.setVisibility(View.INVISIBLE);
        mTimeRed.setVisibility(View.VISIBLE);

        mDayGreen.setVisibility(View.INVISIBLE);
        mDayOrange.setVisibility(View.INVISIBLE);
        mDayRed.setVisibility(View.VISIBLE);

        mDayRed.setText("1 DAY LEFT");
    }

    private void twelveHoursLeft(){
        mTimeGreen.setVisibility(View.INVISIBLE);
        mTimeOrange.setVisibility(View.INVISIBLE);
        mTimeRed.setVisibility(View.VISIBLE);

        mDayGreen.setVisibility(View.INVISIBLE);
        mDayOrange.setVisibility(View.INVISIBLE);
        mDayRed.setVisibility(View.VISIBLE);

        mDayRed.setText("LESS THAN 12 HOURS" );
    }

    private void getPosts(){
        FirebaseDatabase.getInstance().getReference("packs").child(packId)
                .child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postsList.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelPosts modelPosts = ds.getValue(ModelPosts.class);
                            long timecurrent = System.currentTimeMillis();

                            if(timecurrent > modelPosts.getTimestart() && timecurrent < modelPosts.getTimeend()) {
                                postsList.add(modelPosts);

                                adapterFeedPosts = new AdapterFeedPosts(ViewPack.this, postsList);
                                recyclerView.setAdapter(adapterFeedPosts);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void leavePack(){
        FirebaseDatabase.getInstance().getReference("my_packs")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(packId).removeValue();

        FirebaseDatabase.getInstance().getReference("my_packs_members_count")
                .child(packId).removeValue();

        FirebaseDatabase.getInstance().getReference("notifications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(packId).removeValue();

        FirebaseDatabase.getInstance().getReference("pack_members")
                .child(packId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ViewPack.this, "You left the pack", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewPack.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editPack(String packName){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pack_name", packName);

        FirebaseDatabase.getInstance().getReference("packs").child(packId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ViewPack.this, "Saved", Toast.LENGTH_SHORT).show();
                        mEditPackCard.setVisibility(View.INVISIBLE);
                        mLeaveOrEditCard.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void getAllMembers() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pack_members").child(packId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packMembersImages.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPackMembersImages modelPackMembersImages = ds.getValue(ModelPackMembersImages.class);

                    packMembersImages.add(modelPackMembersImages);

                    adapterPackImages = new AdapterPackImages(ViewPack.this, packMembersImages);

                    packImagesRecyclerView.setAdapter(adapterPackImages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkStatus(String packId){
        if(myPackRole.equals("nothing")){
            setConnect();
        }

        FirebaseDatabase.getInstance().getReference("pack_members").child(packId)
                .orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            myPackRole = "" + ds.child("role").getValue();

                            if(myPackRole.equals("member")){
                                setMember();
                            }
                            if(myPackRole.equals("admin")){
                                setAdmin();
                            }
                            if(myPackRole.equals("alpha")){
                                setAlpha();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadMyGroupRole() {
        FirebaseDatabase.getInstance().getReference("pack_members").child(packId)
                .orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            myPackRole = "" + ds.child("role").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("packs");
        ref.orderByChild("pack_id").equalTo(packId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String pack_name = "" + ds.child("pack_name").getValue();
                            String timeStamp = "" + ds.child("timestamp").getValue();
                            String alpha = "" + ds.child("alpha").getValue();
                            String members_count = "" + ds.child("members_count").getValue();
                            String connections_count = "" + ds.child("connections_count").getValue();

                            //get Values
                            mViewPackName.setText(pack_name);

                            FirebaseDatabase.getInstance().getReference("user_account_settings")
                                    .orderByChild("user_id").equalTo(alpha)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds:snapshot.getChildren()){
                                                String alpha_name = "" + ds.child("display_name").getValue();
                                                String alpha_uid = "" + ds.child("user_id").getValue();

                                                if(!alpha_uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                    mAlphaName.setText(alpha_name);
                                                    mAlphaName.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(ViewPack.this, ViewUser.class);
                                                            intent.putExtra("uid", alpha_uid);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }else{
                                                    mAlphaName.setText("Me");
                                                    mAlphaName.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(ViewPack.this, ViewMyself.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
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

    private void setConnect(){
        if(myPackRole.equals("nothing")){
            memberPrivilege.setVisibility(View.GONE);
            mViewPackChat.setVisibility(View.INVISIBLE);
            mViewPackAddLocation.setVisibility(View.INVISIBLE);
            mViewPackAddMembers.setVisibility(View.INVISIBLE);
            mEditPackImageView.setVisibility(View.INVISIBLE);
        }
    }
    private void setMember(){
        if(myPackRole.equals("member")){
            memberPrivilege.setVisibility(View.VISIBLE);
            mViewPackChat.setVisibility(View.VISIBLE);
            mViewPackAddLocation.setVisibility(View.INVISIBLE);
            mViewPackAddMembers.setVisibility(View.INVISIBLE);
            mEditPackImageView.setVisibility(View.VISIBLE);

        }
    }
    private void setAdmin(){
        if(myPackRole.equals("admin")){
            memberPrivilege.setVisibility(View.VISIBLE);
            mViewPackChat.setVisibility(View.VISIBLE);
            mViewPackAddLocation.setVisibility(View.INVISIBLE);
            mViewPackAddMembers.setVisibility(View.VISIBLE);
            mEditPackImageView.setVisibility(View.VISIBLE);

        }
    }
    private void setAlpha(){
        if(myPackRole.equals("alpha")){
            memberPrivilege.setVisibility(View.VISIBLE);
            mViewPackChat.setVisibility(View.VISIBLE);
            mViewPackAddLocation.setVisibility(View.VISIBLE);
            mViewPackAddMembers.setVisibility(View.VISIBLE);
            mEditPackImageView.setVisibility(View.VISIBLE);
        }
    }

    private void getViewPackMembersCount(){
        mMemberCount = 0;

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pack_members").child(packId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot singleSnapshot: snapshot.getChildren()){
                            mMemberCount++;
                        }
                        mViewPackCountMembers.setText(String.valueOf(mMemberCount));
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