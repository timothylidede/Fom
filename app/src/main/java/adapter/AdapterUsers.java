package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.savala.fom.R;
import com.savala.fom.ViewMyself;
import com.savala.fom.ViewUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import models.UserAccountSettings;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    private Context context;
    private List<UserAccountSettings> userList;

    //constructor
    public AdapterUsers(Context context, List<UserAccountSettings> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user)

        View view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String user_Id = userList.get(position).getUser_id();
        String userImageOne = userList.get(position).getImage_one();
        String userImageTwo = userList.get(position).getImage_two();
        String userImageThree = userList.get(position).getImage_three();
        String userDisplayName = userList.get(position).getDisplay_name();

        try{
            Picasso.get().load(userImageOne)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(holder.mRowUserImageOne);
        }catch(Exception e){}

        try{
            Picasso.get().load(userImageTwo)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(holder.mRowUserImageTwo);
        }catch(Exception e){}

        try{
            Picasso.get().load(userImageThree)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(holder.mRowUserImageThree);
        }catch(Exception e){}

        holder.mRowUserDisplayName.setText(userDisplayName);

        if(!user_Id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewUser.class);
                    intent.putExtra("uid", user_Id);
                    context.startActivity(intent);
                }
            });
        }else{
            holder.profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewMyself.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        private ImageView mRowUserImageOne, mRowUserImageTwo, mRowUserImageThree;
        private TextView mRowUserDisplayName, mRowUserUsername, mRowUserDescription;
        private LinearLayout profileLayout;
        private RelativeLayout rowUserNameLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mRowUserImageOne = itemView.findViewById(R.id.row_user_image_one);
            mRowUserImageTwo = itemView.findViewById(R.id.row_user_image_two);
            mRowUserImageThree = itemView.findViewById(R.id.row_user_image_three);

            mRowUserDisplayName = itemView.findViewById(R.id.row_user_display_name);

            profileLayout = itemView.findViewById(R.id.profile_layout);
            rowUserNameLayout = itemView.findViewById(R.id.row_user_name_layout);
        }


    }
}
