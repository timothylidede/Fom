package adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
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
import com.savala.fom.ViewMyself;
import com.savala.fom.ViewUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import models.ModelPackChat;

public class AdapterPackChat extends RecyclerView.Adapter<AdapterPackChat.PackChatHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<ModelPackChat> packChatList;

    private FirebaseAuth firebaseAuth;

    //constructor
    public AdapterPackChat(Context context, List<ModelPackChat> packChatList) {
        this.context = context;
        this.packChatList = packChatList;

        firebaseAuth = FirebaseAuth.getInstance();
    }


    @NonNull
    @Override
    public PackChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_packchat_right, parent, false);
            return new PackChatHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_packchat_left, parent, false);
            return new PackChatHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final PackChatHolder holder, int position) {
        // get data
        ModelPackChat modelPackChat = packChatList.get(position);
        String message = modelPackChat.getMessage();
        String timestamp = modelPackChat.getTimestamp();
        String senderUid = modelPackChat.getSender();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        //set data
        holder.mMessage.setText(message);
        holder.mTime.setText(dateTime);

        if(!senderUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.mUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewUser.class);
                    intent.putExtra("uid", senderUid);
                    context.startActivity(intent);
                }
            });
        }else{
            holder.mUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewMyself.class);
                    context.startActivity(intent);
                }
            });
        }

        setUsername(modelPackChat, holder);
    }

    private void setUsername(ModelPackChat modelPackChat, PackChatHolder holder) {
        //get sender info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_account_settings");
        ref.orderByChild("user_id").equalTo(modelPackChat.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String username = ""+ ds.child("display_name").getValue();
                            holder.mUsername.setText(username);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return packChatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(packChatList.get(position).getSender().equals(firebaseAuth.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    class PackChatHolder extends RecyclerView.ViewHolder{
        private TextView mUsername, mMessage, mTime;

        public PackChatHolder(@NonNull View itemView) {
            super(itemView);

            mUsername = itemView.findViewById(R.id.chat_username);
            mMessage = itemView.findViewById(R.id.chat_message);
            mTime = itemView.findViewById(R.id.chat_time);
        }
    }
}

