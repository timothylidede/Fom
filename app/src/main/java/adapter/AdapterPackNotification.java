package adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savala.fom.R;
import com.savala.fom.ViewUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import models.ModelPackNotification;

public class AdapterPackNotification extends RecyclerView.Adapter<AdapterPackNotification.NotificationHolder> {

    private Context context;
    private ArrayList<ModelPackNotification> notificationList;

    //constructor
    public AdapterPackNotification(Context context, ArrayList<ModelPackNotification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user)

        View view = LayoutInflater.from(context).inflate(R.layout.row_pack_notification, parent, false);

        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationHolder holder, int position) {
        ModelPackNotification model = notificationList.get(position);
        String pack_id = model.getPack_id();
        String sender_id = model.getSender_id();
        String timestamp = model.getTimestamp();
        String notification = model.getNotification();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();


        setUserNameAndImage(holder, model);
        setPackName(holder, model);
        holder.mNotification.setText(notification);
        holder.mTime.setText(dateTime);

        holder.mSenderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewUser.class);
                intent.putExtra("uid", sender_id);
                context.startActivity(intent);
            }
        });
        holder.mSenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewUser.class);
                intent.putExtra("uid", sender_id);
                context.startActivity(intent);
            }
        });

    }

    private void setPackName(NotificationHolder holder, ModelPackNotification model) {
        FirebaseDatabase.getInstance().getReference("packs")
                .orderByChild("pack_id").equalTo(model.getPack_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String pack_name = "" + ds.child("pack_name").getValue();

                            holder.mPackName.setText(pack_name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setUserNameAndImage(NotificationHolder holder, ModelPackNotification model) {
        FirebaseDatabase.getInstance().getReference("user_account_settings")
                .orderByChild("user_id").equalTo(model.getSender_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String display_name = "" +ds.child("display_name").getValue();
                            String image_one = "" + ds.child("image_one").getValue();

                            holder.mSenderName.setText(display_name);

                            try{
                                Picasso.get().load(image_one)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(holder.mSenderImage);
                            }catch(Exception e) {
                                holder.mSenderImage.setImageResource(R.drawable.ic_baseline_person_24);
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
        return notificationList.size();
    }

    class NotificationHolder extends RecyclerView.ViewHolder{
        private TextView mSenderName, mNotification, mTime, mPackName;
        private ImageView mSenderImage;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            mPackName = itemView.findViewById(R.id.pack_name);
            mSenderName = itemView.findViewById(R.id.sender_name);
            mSenderImage = itemView.findViewById(R.id.sender_image);
            mNotification = itemView.findViewById(R.id.notification);
            mTime = itemView.findViewById(R.id.time);

        }
    }
}
