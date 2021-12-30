package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.savala.fom.ViewMyself;
import com.savala.fom.ViewUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import models.ModelPackMembers;

public class AdapterPackMembers extends RecyclerView.Adapter<AdapterPackMembers.PackMembersHolder> {

    private Context context;
    private ArrayList<ModelPackMembers> packMembersList;

    //constructor


    public AdapterPackMembers(Context context, ArrayList<ModelPackMembers> packMembersList) {
        this.context = context;
        this.packMembersList = packMembersList;
    }

    @NonNull
    @Override
    public PackMembersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user)

        View view = LayoutInflater.from(context).inflate(R.layout.row_connection, parent, false);

        return new PackMembersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PackMembersHolder holder, int position) {
        // get data
        final ModelPackMembers packMembers = packMembersList.get(position);
        String role = packMembers.getRole();
        String uid = packMembers.getUid();
        String timestamp = packMembers.getTimestamp();
        String pack_id = packMembers.getPack_id();

        //click potential to open to user
        if(!uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.connectionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewUser.class);
                    intent.putExtra("uid", uid);
                    context.startActivity(intent);
                }
            });
        }else{
            holder.connectionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewMyself.class);
                    context.startActivity(intent);
                }
            });
        }

        setConnectionDetails(packMembers, holder);
    }

    private void setConnectionDetails(ModelPackMembers packMembers, PackMembersHolder holder) {
        //get user info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_account_settings");
        ref.orderByChild("user_id").equalTo(packMembers.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String connectionName = "" + ds.child("display_name").getValue();
                            String connectionImageOne = ""+ds.child("image_one").getValue();
                            String connectionImageTwo = ""+ds.child("image_two").getValue();
                            String connectionImageThree = ""+ds.child("image_three").getValue();

                            holder.mRowConnectionDisplayName.setText(connectionName);
                            try{
                                Picasso.get().load(connectionImageOne)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(holder.mRowConnectionImageOne);
                            }catch(Exception e){
                                holder.mRowConnectionImageOne.setImageResource(R.drawable.ic_baseline_person_24);
                            }
                            try{
                                Picasso.get().load(connectionImageTwo)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(holder.mRowConnectionImageTwo);
                            }catch(Exception e){
                                holder.mRowConnectionImageTwo.setImageResource(R.drawable.ic_baseline_person_24);
                            }
                            try{
                                Picasso.get().load(connectionImageThree)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(holder.mRowConnectionImageThree);
                            }catch(Exception e){
                                holder.mRowConnectionImageThree.setImageResource(R.drawable.ic_baseline_person_24);
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
        return packMembersList.size();
    }

    class PackMembersHolder extends RecyclerView.ViewHolder{
        private ImageView mRowConnectionImageOne, mRowConnectionImageTwo, mRowConnectionImageThree;
        private TextView mRowConnectionDisplayName, mRowConnectionDescription, mRowConnectionUsername;
        private LinearLayout connectionLayout;

        public PackMembersHolder(@NonNull View itemView) {
            super(itemView);

            mRowConnectionImageOne = itemView.findViewById(R.id.row_connection_image_one);
            mRowConnectionImageTwo = itemView.findViewById(R.id.row_connection_image_two);
            mRowConnectionImageThree = itemView.findViewById(R.id.row_connection_image_three);

            mRowConnectionDisplayName = itemView.findViewById(R.id.row_connection_display_name);

            connectionLayout = itemView.findViewById(R.id.connection_layout);
        }
    }
}
