package fragment.connections;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.savala.fom.R;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterUsers;
import models.UserAccountSettings;


public class LocationFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private AdapterUsers adapterUsers;
    private List<UserAccountSettings> userList;

    public LocationFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        //init recyclerview
        recyclerView = view.findViewById(R.id.connect_users_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);

        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init userList
        userList = new ArrayList<>();

        //get all users
        getAllUsers();

        searchView = (SearchView) view.findViewById(R.id.connect_search_icon);
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
                    searchUser(query);
                }else{
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchUser(newText);
                }else{
                    getAllUsers();
                }
                return false;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllUsers();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void searchUser(String s) {
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user_account_settings");
        //get all data from path
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    UserAccountSettings userAccountSettings = ds.getValue(UserAccountSettings.class);

                    if(userAccountSettings.getDisplay_name().toLowerCase().contains(s.toLowerCase())){
                        userList.add(userAccountSettings);
                    }

                    //adapters
                    adapterUsers = new AdapterUsers(getActivity(), userList);

                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllUsers() {
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user_account_settings");
        //get all data from path
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    UserAccountSettings userAccountSettings = ds.getValue(UserAccountSettings.class);

                    userList.add(userAccountSettings);

                    //adapters
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
