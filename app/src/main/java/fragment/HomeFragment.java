package fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.savala.fom.MyConnections;
import com.savala.fom.MyPacks;
import com.savala.fom.R;
import com.savala.fom.Sign;
import com.savala.fom.ViewMyself;

import java.util.ArrayList;

import adapter.AdapterFeedPosts;
import models.ModelPosts;
import utils.FirebaseMethods;

import static android.content.ContentValues.TAG;

public class HomeFragment extends BaseFragment {
    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView mViewMyself;
    private ImageView mMyPacks;
    private ImageView mMyConnections;

    RecyclerView recyclerView;
    RecyclerView.RecyclerListener recyclerListener;
    AdapterFeedPosts adapterFeedPosts;
    ArrayList<ModelPosts> postsList;

    private ProgressBar mProgressBar;
    private SearchView searchView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    public static HomeFragment create(){
        return new HomeFragment();
    }
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mFirebaseMethods = new FirebaseMethods(getActivity());

        mAuth = FirebaseAuth.getInstance();
        mProgressBar = root.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        setupFirebaseAuth();
        checkCurrentUser();

        //init recyclerview
        recyclerView = root.findViewById(R.id.feed_posts);
        recyclerView.setNestedScrollingEnabled(false);
        mViewMyself = (ImageView) root.findViewById(R.id.view_myself);
        mMyPacks = (ImageView) root.findViewById(R.id.my_packs);
        mMyConnections = (ImageView) root.findViewById(R.id.my_connections);

        //set its properties
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //init userList
        postsList = new ArrayList<>();

        getPosts();

        FirebaseDatabase.getInstance().getReference("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                        }else{
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        RelativeLayout layout = (RelativeLayout) root.findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        RelativeLayout layout3 = (RelativeLayout) root.findViewById(R.id.layout3);
        AnimationDrawable animationDrawable3 = (AnimationDrawable) layout3.getBackground();
        animationDrawable3.setEnterFadeDuration(3000);
        animationDrawable3.setExitFadeDuration(3000);
        animationDrawable3.start();

        mViewMyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ViewMyself.class);
                startActivity(intent);
            }
        });
        mMyPacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyPacks.class);
                startActivity(intent);
            }
        });
        mMyConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyConnections.class);
                startActivity(intent);
            }
        });

        searchView = (SearchView) root.findViewById(R.id.fom_activity_search_icon);
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
                    long timecurrent = System.currentTimeMillis();

                    if(timecurrent > modelPosts.getTimestart() && timecurrent < modelPosts.getTimeend()) {
                        if (modelPosts.getPlace_name().toLowerCase().contains(s.toLowerCase())) {
                            postsList.add(modelPosts);
                        }
                        //adapters
                        adapterFeedPosts = new AdapterFeedPosts(getActivity(), postsList);

                        //refresh adapter
                        adapterFeedPosts.notifyDataSetChanged();
                        //set adapter to recycler view
                        recyclerView.setAdapter(adapterFeedPosts);
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPosts() {
        FirebaseDatabase.getInstance().getReference("posts").orderByChild("count")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postsList.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelPosts modelPosts = ds.getValue(ModelPosts.class);
                            long timecurrent = System.currentTimeMillis();

                            if(timecurrent > modelPosts.getTimestart() && timecurrent < modelPosts.getTimeend()) {
                                postsList.add(modelPosts);

                                adapterFeedPosts = new AdapterFeedPosts(getActivity(), postsList);
                                recyclerView.setAdapter(adapterFeedPosts);

                                adapterFeedPosts = new AdapterFeedPosts(getActivity(), postsList);
                                recyclerView.setAdapter(adapterFeedPosts);
                            }
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
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
            Intent intent = new Intent(getActivity(), Sign.class);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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
