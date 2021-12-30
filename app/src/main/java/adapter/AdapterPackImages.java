package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import models.ModelPackMembersImages;

public class AdapterPackImages extends RecyclerView.Adapter<AdapterPackImages.PackImagesHolder> {

    private Context context;
    private ArrayList<ModelPackMembersImages> packImagesList;

    //constructor

    public AdapterPackImages(Context context, ArrayList<ModelPackMembersImages> packImagesList) {
        this.context = context;
        this.packImagesList = packImagesList;
    }

    @NonNull
    @Override
    public PackImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user)

        View view = LayoutInflater.from(context).inflate(R.layout.row_pack_images, parent, false);

        return new PackImagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PackImagesHolder holder, int position) {
        // get data
        final ModelPackMembersImages packImages = packImagesList.get(position);
        String memberUid = packImages.getUid();

        //click potential to open to user
        if(!memberUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.mRowPackImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewUser.class);
                    intent.putExtra("uid", memberUid);
                    context.startActivity(intent);
                }
            });
        }else{
            holder.mRowPackImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewMyself.class);
                    context.startActivity(intent);
                }
            });
        }

        setImages(packImages, holder);
    }

    private void setImages(ModelPackMembersImages packImages, PackImagesHolder holder) {
        //get user info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_account_settings");
        ref.orderByChild("user_id").equalTo(packImages.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String image = ""+ds.child("image_one").getValue();

                            try{
                                Picasso.get().load(image)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(holder.mRowPackImage);
                            }catch(Exception e){
                                holder.mRowPackImage.setImageResource(R.drawable.ic_baseline_person_24);
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
        return packImagesList.size();
    }

    class PackImagesHolder extends RecyclerView.ViewHolder{
        private ImageView mRowPackImage;

        public PackImagesHolder(@NonNull View itemView) {
            super(itemView);

            mRowPackImage = itemView.findViewById(R.id.row_pack_image);
        }
    }
}
