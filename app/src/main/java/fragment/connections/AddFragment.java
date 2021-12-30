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

import adapter.AdapterPacks;
import models.ModelPack;


public class AddFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private int no = 0;

    private RecyclerView packRecyclerView;

    private SearchView searchView;

    private AdapterPacks adapterPacks;

    private List<ModelPack> packList;

    private FirebaseAuth firebaseAuth;

    public AddFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        //init recyclerview
        packRecyclerView = view.findViewById(R.id.connect_pack_recycler_view);
        packRecyclerView.setNestedScrollingEnabled(false);

        //set its properties
        packRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        packRecyclerView.setLayoutManager(layoutManager);

        //init userList
        packList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        //get all users
        getAllPacks();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPacks();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        searchView = (SearchView) view.findViewById(R.id.pack_search_icon);
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
                    getAllPacks();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchUser(newText);
                }else{
                    getAllPacks();
                }
                return false;
            }
        });

        return view;
    }

    private void searchUser(String s) {
        //get current user
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("packs");
        //get all data from path
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPack modelPack = ds.getValue(ModelPack.class);
                    long timecurrent = System.currentTimeMillis();

                    if(timecurrent > modelPack.getTimestart() && timecurrent < modelPack.getTimeend()) {
                        if (modelPack.getPack_name().toLowerCase().contains(s.toLowerCase())) {
                            packList.add(modelPack);
                        }
                        //adapters
                        adapterPacks = new AdapterPacks(getActivity(), packList);

                        //refresh adapter
                        adapterPacks.notifyDataSetChanged();

                        //set adapter to recycler view
                        packRecyclerView.setAdapter(adapterPacks);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllPacks() {
        //get path of database
        FirebaseDatabase.getInstance().getReference("packs").orderByChild("count")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPack modelPack = ds.getValue(ModelPack.class);
                    long timecurrent = System.currentTimeMillis();

                    if(timecurrent > modelPack.getTimestart() && timecurrent < modelPack.getTimeend()){
                        packList.add(modelPack);

                        //adapters
                        adapterPacks = new AdapterPacks(getActivity(), packList);

                        //set adapter to recycler view
                        packRecyclerView.setAdapter(adapterPacks);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
