package com.savala.fom;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import adapter.AdapterMyPacks;
import models.ModelMyPacks;
import utils.FirebaseMethods;

import static android.content.ContentValues.TAG;

public class MyPacks extends AppCompatActivity {

    private SearchView searchView;
    private CardView myZeroPacks;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mStorageReference;
    private DatabaseReference myRef;
    private String userID;
    private FirebaseMethods mFirebaseMethods;

    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    RecyclerView recyclerView;
    AdapterMyPacks adapterPacks;
    ArrayList<ModelMyPacks> packsList;

    private int mMyPacksCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_packs);

        myZeroPacks = (CardView) findViewById(R.id.my_zero_packs);

        mFirebaseMethods = new FirebaseMethods(MyPacks.this);

        //init recyclerview
        recyclerView = findViewById(R.id.packs_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);

        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //init userList
        packsList = new ArrayList<>();

        getPacks();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPacks();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                finish();
            }

        });

        setupFirebaseAuth();

    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

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
    }


    private void getPacks(){
        mAuth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //get current user_id
        final String mPot = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //get path of database
        DatabaseReference myRefPot = FirebaseDatabase.getInstance().getReference("my_packs").child(mPot);

        myRefPot.orderByChild("user_id").equalTo(mPot)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    packsList.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        //get data

                        ModelMyPacks modelPacks = ds.getValue(ModelMyPacks.class);
                        long timecurrent = System.currentTimeMillis();

                        if (timecurrent > modelPacks.getTimestart() && timecurrent < modelPacks.getTimeend()) {
                            packsList.add(modelPacks);

                            //adapters
                            adapterPacks = new AdapterMyPacks(MyPacks.this, packsList);

                            //set adapter to recycler view
                            recyclerView.setAdapter(adapterPacks);
                        }
                        if(timecurrent > modelPacks.getTimeend()){
                            myZeroPacks.setVisibility(View.INVISIBLE);
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}