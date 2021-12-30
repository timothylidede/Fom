package com.savala.fom;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.AdapterFeedPosts;
import models.ModelPosts;

public class FomActivity extends AppCompatActivity {
    private ImageView mFinish;
    private LinearLayout mNoPosts;

    RecyclerView recyclerView;
    RecyclerView.RecyclerListener recyclerListener;
    AdapterFeedPosts adapterFeedPosts;
    ArrayList<ModelPosts> postsList;

    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_fom);

        mFinish = findViewById(R.id.finish);
        mNoPosts = findViewById(R.id.no_posts);

        //init recyclerview
        recyclerView = findViewById(R.id.feed_posts);
        recyclerView.setNestedScrollingEnabled(false);

        //set its properties
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FomActivity.this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //init userList
        postsList = new ArrayList<>();

        getPosts();

        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseDatabase.getInstance().getReference("posts")
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

        searchView = (SearchView) findViewById(R.id.fom_activity_search_icon);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchPost(query);
                }else{
                    getPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchPost(newText);
                }else{
                    getPosts();
                }
                return false;
            }
        });
    }

    private void searchPost(String s) {
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("posts");
        //get all data from path
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPosts modelPosts = ds.getValue(ModelPosts.class);


                    if(modelPosts.getPlace_name().toLowerCase().contains(s.toLowerCase())){
                        postsList.add(modelPosts);
                    }

                    //adapters
                    adapterFeedPosts = new AdapterFeedPosts(FomActivity.this, postsList);

                    //refresh adapter
                    adapterFeedPosts.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterFeedPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPosts() {
        FirebaseDatabase.getInstance().getReference("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postsList.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelPosts modelPosts = ds.getValue(ModelPosts.class);
                            long timecurrent = System.currentTimeMillis();

                            if(timecurrent > modelPosts.getTimestart() && timecurrent < modelPosts.getTimeend()) {
                                postsList.add(modelPosts);

                                adapterFeedPosts = new AdapterFeedPosts(FomActivity.this, postsList);
                                recyclerView.setAdapter(adapterFeedPosts);
                            }
                        }

                        adapterFeedPosts = new AdapterFeedPosts(FomActivity.this, postsList);
                        recyclerView.setAdapter(adapterFeedPosts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}