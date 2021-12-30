package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savala.fom.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import models.ModelAddMembers;

public class AdapterAddMember extends RecyclerView.Adapter<AdapterAddMember.AddMemberHolder> {

    private Context context;
    private ArrayList<ModelAddMembers> userList;
    private String groupId, myGroupRole;  //alpha, admin, participant

    //constructor
    public AdapterAddMember(Context context, ArrayList<ModelAddMembers> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public AddMemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user)

        View view = LayoutInflater.from(context).inflate(R.layout.row_add_member, parent, false);

        return new AddMemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddMemberHolder holder, int position) {
        // get data
        final ModelAddMembers user = userList.get(position);
        /*String userDisplayName = user.getDisplay_name();
        String userUsername = user.getUsername();
        String userImageOne = user.getImage_one();
        String userImageTwo = user.getImage_two();
        String userImageThree = user.getImage_three();*/
        String userUid = user.getUser_id();

        //set data
        setConnectionsDetails(user, holder);

        checkIfAlreadyExists(user, holder);

        //handle click
        holder.mRowAddMemberNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Check if user already added or not
                If added, show remove participant/ make admin/ remove admin (Admin will not be able to change role of creator)
                if not added, show add option
                 */
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_members");
                ref.child(groupId).child(userUid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    //user exists/ not member
                                    String hisPreviousRole = "" + snapshot.child("role").getValue();

                                    //options to display in dialog
                                    String[] options;

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Option");
                                    if(myGroupRole.equals("alpha")){
                                        if(hisPreviousRole.equals("admin")){
                                            //im alpha, he is admin
                                            options = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //handle item clicks
                                                    if(which == 0){
                                                        //remove admin clicked
                                                        removeAdmin(user);
                                                        removeAdminNotification(userUid, "removed you as an admin of");
                                                    }else{
                                                        //remove user clicked
                                                        removeMember(user);
                                                        removeMemberNotification(userUid, "removed you as a member of");
                                                    }
                                                }
                                            }).show();
                                        }else if (hisPreviousRole.equals("member")){
                                            //im alpha, he is participant
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //handle item clicks
                                                    if(which == 0){
                                                        //Make admin clicked
                                                        makeAdmin(user);
                                                        makeAdminNotification(userUid, "made you an admin of");
                                                    }else{
                                                        //remove user clicked
                                                        removeMember(user);
                                                        removeMemberNotification(userUid, "removed you as a member of");
                                                    }
                                                }
                                            }).show();
                                        }
                                    }else if(myGroupRole.equals("admin")){
                                        if(hisPreviousRole.equals("alpha")){
                                            //im admin he is creator
                                            Toast.makeText(context, "Alpha", Toast.LENGTH_SHORT).show();
                                        }else if(hisPreviousRole.equals("admin")){
                                            //im admin, he is admin
                                            options = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //handle item clicks
                                                    if(which == 0){
                                                        //remove admin clicked
                                                        removeAdmin(user);
                                                        removeAdminNotification(userUid, "removed you as an admin of");
                                                    }else{
                                                        //remove user clicked
                                                        removeMember(user);
                                                        removeMemberNotification(userUid, "removed you as a member of");
                                                    }
                                                }
                                            }).show();
                                        }else if(hisPreviousRole.equals("member")){
                                            //im admin, he is participant
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //handle item clicks
                                                    if(which == 0){
                                                        //Make admin clicked
                                                        makeAdmin(user);
                                                        makeAdminNotification(userUid, "made you an admin of");
                                                    }else{
                                                        //remove user clicked
                                                        removeMember(user);
                                                        removeMemberNotification(userUid, "removed you as a member of");
                                                    }
                                                }
                                            }).show();
                                        }
                                    }
                                }else{
                                    //user doesnt exist/not member.. add
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add Member")
                                            .setMessage("Add user to pack?")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //add user
                                                    addMember(user);
                                                    addMemberNotification(userUid, "added you as a member of");
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    private void addMemberNotification(String userUid, String notification) {
        String timestamp =  "" + System.currentTimeMillis();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pack_id", groupId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("sender_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("notification", notification);

        FirebaseDatabase.getInstance().getReference("notifications").child(userUid)
                .child(timestamp).setValue(hashMap);
    }

    private void makeAdminNotification(String userUid, String notification) {
        String timestamp =  "" + System.currentTimeMillis();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pack_id", groupId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("sender_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("notification", notification);

        FirebaseDatabase.getInstance().getReference("notifications").child(userUid)
                .child(timestamp).setValue(hashMap);
    }

    private void removeMemberNotification(String userUid, String notification) {
        String timestamp =  "" + System.currentTimeMillis();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pack_id", groupId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("sender_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("notification", notification);

        FirebaseDatabase.getInstance().getReference("notifications").child(userUid)
                .child(timestamp).setValue(hashMap);
    }

    private void removeAdminNotification(String userUid, String notification) {
        String timestamp =  "" + System.currentTimeMillis();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pack_id", groupId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("sender_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("notification", notification);

        FirebaseDatabase.getInstance().getReference("notifications").child(userUid)
                .child(timestamp).setValue(hashMap);
    }

    private void setConnectionsDetails(ModelAddMembers user, AddMemberHolder holder) {
        //get user info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_account_settings");
        ref.orderByChild("user_id").equalTo(user.getUser_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String userDisplayName = "" + ds.child("display_name").getValue();
                            String userImageOne = ""+ds.child("image_one").getValue();
                            String userImageTwo = ""+ds.child("image_two").getValue();
                            String userImageThree = ""+ds.child("image_three").getValue();

                            holder.mRowAddMemberDisplayName.setText(userDisplayName);

                            try{
                                Picasso.get().load(userImageOne)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(holder.mRowAddMemberImageOne);
                            }catch(Exception e){}

                            try{
                                Picasso.get().load(userImageTwo)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(holder.mRowAddMemberImageTwo);
                            }catch(Exception e){}

                            try{
                                Picasso.get().load(userImageThree)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(holder.mRowAddMemberImageThree);
                            }catch(Exception e){}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addMember(ModelAddMembers user) {
        //setup user data
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", user.getUser_id());
        hashMap.put("role", "member");
        hashMap.put("pack_id", groupId);
        hashMap.put("timestamp", "" + timestamp);

        //add user
        FirebaseDatabase.getInstance().getReference("packs")
                .orderByChild("pack_id").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String alpha = "" + ds.child("alpha").getValue();

                            HashMap<String, String> secondHashMap = new HashMap<>();
                            secondHashMap.put("pack_id", groupId);
                            secondHashMap.put("user_id", user.getUser_id());
                            secondHashMap.put("alpha", alpha);

                            FirebaseDatabase.getInstance().getReference("my_packs")
                                    .child(user.getUser_id()).child(groupId)
                                    .setValue(secondHashMap);

                            FirebaseDatabase.getInstance().getReference("my_packs_members_count")
                                    .child(groupId).child(user.getUser_id())
                                    .setValue(secondHashMap);


                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_members")
                                    .child(groupId);
                            ref.child(user.getUser_id()).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //added successfully
                                            Toast.makeText(context, "New member added to the pack", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void makeAdmin(ModelAddMembers user) {
        //setup data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "admin");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_members")
                .child(groupId);
        ref.child(user.getUser_id()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //make admin
                        Toast.makeText(context, "User is now an admin", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed making admin
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeMember(ModelAddMembers user) {
        //remove member from pack
        FirebaseDatabase.getInstance().getReference("my_packs")
                .child(user.getUser_id()).child(groupId)
                .removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_members")
                .child(groupId);
        ref.child(user.getUser_id()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //removed successfully
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeAdmin(ModelAddMembers user) {
        //setup data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "member");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_members");
        ref.child(user.getUser_id()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //make admin
                        Toast.makeText(context, "User is no longer an admin", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed making admin
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfAlreadyExists(ModelAddMembers user, AddMemberHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_members");
        ref.child(groupId).child(user.getUser_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //already exists
                            String hisRole = "" + snapshot.child("role").getValue();
                            holder.mRowAddMemberStatus.setText(hisRole);
                        }else{
                            //doesn't exist
                            holder.mRowAddMemberStatus.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class AddMemberHolder extends RecyclerView.ViewHolder{
        private ImageView mRowAddMemberImageOne, mRowAddMemberImageTwo, mRowAddMemberImageThree;
        private LinearLayout mRowAddMemberNameLayout;
        private TextView mRowAddMemberDisplayName, mRowAddMemberStatus;

        public AddMemberHolder(@NonNull View itemView) {
            super(itemView);

            mRowAddMemberDisplayName = itemView.findViewById(R.id.row_add_member_display_name);
            mRowAddMemberImageOne = itemView.findViewById(R.id.row_add_member_image_one);
            mRowAddMemberImageTwo = itemView.findViewById(R.id.row_add_member_image_two);
            mRowAddMemberImageThree = itemView.findViewById(R.id.row_add_member_user_image_three);
            mRowAddMemberNameLayout = itemView.findViewById(R.id.profile_layout);
            mRowAddMemberStatus = itemView.findViewById(R.id.row_add_member_status);
        }
    }
}
