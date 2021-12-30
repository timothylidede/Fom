package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savala.fom.R;
import com.savala.fom.ViewPack;

import java.util.HashMap;
import java.util.List;

import models.ModelPack;

public class AdapterPacks extends RecyclerView.Adapter<AdapterPacks.PackHolder> {

    private Context context;
    private List<ModelPack> packList;

    private int memberCount = 0;
    //constructor


    public AdapterPacks(Context context, List<ModelPack> packList) {
        this.context = context;
        this.packList = packList;
    }

    @NonNull
    @Override
    public PackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user)

        View view = LayoutInflater.from(context).inflate(R.layout.row_pack, parent, false);

        return new PackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PackHolder holder, int position) {
        // get data
        final ModelPack pack = packList.get(position);
        String packName = pack.getPack_name();
        final String packId = pack.getPack_id();

        //set data
        holder.mRowPackName.setText(packName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewPack.class);
                intent.putExtra("packId", packId);
                context.startActivity(intent);
            }
        });

        setName(pack, holder);
        getMemberCount(pack, holder);
    }

    private void getMemberCount(ModelPack pack, PackHolder holder) {

        FirebaseDatabase.getInstance().getReference("pack_members")
                .child(pack.getPack_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            memberCount++;
                        }
                        holder.mRowPackMemberCount.setText(String.valueOf(memberCount));

                        long count = memberCount;

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("count", count);

                        FirebaseDatabase.getInstance().getReference("packs")
                                .child(pack.getPack_id())
                                .updateChildren(hashMap);
                        memberCount = 0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setName(ModelPack pack, PackHolder holder) {
        //get user info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_account_settings");
        ref.orderByChild("user_id").equalTo(pack.getAlpha())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String alphaName = ""+ ds.child("display_name").getValue();
                            String alphaUid = "" + ds.child("user_id").getValue();

                            if(!alphaUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                holder.mRowPackAlphaName.setText(alphaName);
                            }else{
                                holder.mRowPackAlphaName.setText("Me");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return packList.size();
    }

    class PackHolder extends RecyclerView.ViewHolder{
        private TextView mRowPackName, mRowPackAlphaName;
        private TextView mRowPackMemberCount;

        public PackHolder(@NonNull View itemView) {
            super(itemView);

            mRowPackAlphaName = itemView.findViewById(R.id.row_pack_display_name_alpha);
            mRowPackName = itemView.findViewById(R.id.row_pack_display_name);
            mRowPackMemberCount = itemView.findViewById(R.id.row_pack_member_count);
        }
    }
}
