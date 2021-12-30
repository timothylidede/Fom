package adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.savala.fom.Likes;
import com.savala.fom.R;
import com.savala.fom.ViewPack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import models.ModelPosts;

public class AdapterFeedPosts extends RecyclerView.Adapter<AdapterFeedPosts.PostsHolder> {

    private Context context;
    private ArrayList<ModelPosts> postsList;

    private DatabaseReference likesRef;
    private DatabaseReference postsRef;

    private String currentState = "not_liked";
    private int mLikeCount = 0;

    //constructor
    public AdapterFeedPosts(Context context, ArrayList<ModelPosts> postsList) {
        this.context = context;
        this.postsList = postsList;

        likesRef = FirebaseDatabase.getInstance().getReference("likes");
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
    }

    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user)

        View view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false);

        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostsHolder holder, int position) {
        // get data
        final ModelPosts posts = postsList.get(position);
        String post_id = posts.getPost_id();
        String pack_id = posts.getPack_id();

        holder.bindView(position);

        setPackDetails(posts, holder);
        setLikes(holder, post_id);
        getLikesCount(post_id, holder);

        //click to open to pack
        holder.mPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewPack.class);
                intent.putExtra("packId", pack_id);
                intent.putExtra("postId", post_id);
                context.startActivity(intent);
            }
        });

        holder.mViewLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Likes.class);
                intent.putExtra("postId", post_id);
                context.startActivity(intent);
            }
        });

        holder.mPostNotLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

                FirebaseDatabase.getInstance().getReference()
                        .child("likes")
                        .child(post_id)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(hashMap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    currentState = "liked";
                                    holder.mPostNotLiked.setVisibility(View.INVISIBLE);
                                    holder.mPostLiked.setVisibility(View.VISIBLE);
                                    getLikesCount(post_id, holder);
                                }
                            }
                        });
            }
        });

        holder.mPostLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("likes")
                        .child(post_id)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    currentState = "not_liked";
                                    holder.mPostNotLiked.setVisibility(View.VISIBLE);
                                    holder.mPostLiked.setVisibility(View.INVISIBLE);
                                    getLikesCount(post_id, holder);
                                }
                            }
                        });
            }
        });
    }

    private void setLikes(PostsHolder holder, String post_id){
        FirebaseDatabase.getInstance().getReference()
                .child("likes")
                .child(post_id)
                .orderByChild("user_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            currentState = "liked";
                            holder.mPostLiked.setVisibility(View.VISIBLE);
                            holder.mPostNotLiked.setVisibility(View.INVISIBLE);
                        }else{
                            currentState = "not_liked";
                            holder.mPostLiked.setVisibility(View.INVISIBLE);
                            holder.mPostNotLiked.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getLikesCount(String post_id, PostsHolder holder){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("likes")
                .child(post_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    mLikeCount++;
                }

                holder.mLikesCount.setText(String.valueOf(mLikeCount));

                long count = mLikeCount;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("count", count);

                FirebaseDatabase.getInstance().getReference("posts")
                        .child(post_id)
                        .updateChildren(hashMap);

                mLikeCount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setPackDetails(ModelPosts posts, PostsHolder holder) {
        //get pack info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("packs");
        ref.orderByChild("pack_id").equalTo(posts.getPack_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String pack_name = "" + ds.child("pack_name").getValue();

                            holder.mRowPackName.setText(pack_name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onViewRecycled(@NonNull PostsHolder holder) {
        AdapterFeedPosts.PostsHolder mapHolder = (AdapterFeedPosts.PostsHolder) holder;
        if (mapHolder != null && mapHolder.map != null) {
            // Clear the map and free up resources by changing the map type to none.
            // Also reset the map when it gets reattached to layout, so the previous map would
            // not be displayed.
            mapHolder.map.clear();
            mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    class PostsHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        private TextView mRowPackName, mRowTimePosted, mRowPostDate, mRowPostTime;

        private ImageView mPostLiked, mPostNotLiked;
        private TextView mLikesCount;
        private TextView mViewLikes;

        private RelativeLayout mPostLayout;
        GoogleMap map;
        MapView mapView;
        View layout;
        int position;

        public PostsHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;

            mapView = (MapView) layout.findViewById(R.id.map_view);
            mRowPackName = layout.findViewById(R.id.row_post_pack_name);
            mRowTimePosted = layout.findViewById(R.id.row_post_timestamp);
            mRowPostDate = layout.findViewById(R.id.row_post_date);
            mRowPostTime = layout.findViewById(R.id.row_post_time);
            mPostLayout = layout.findViewById(R.id.post_layout);
            mPostLiked = layout.findViewById(R.id.post_liked);
            mPostNotLiked = layout.findViewById(R.id.post_not_liked);
            mLikesCount = layout.findViewById(R.id.row_post_count);
            mViewLikes = layout.findViewById(R.id.view_likes);

            if (mapView != null) {
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            setMapLocation();
        }

        private void setMapLocation(){
            if (map == null) return;

            ModelPosts data = (ModelPosts) mapView.getTag();
            if (data == null) return;

            String place_name = data.getPlace_name();
            float place_latitude = Float.parseFloat(data.getPlace_latitude());
            float place_longitude = Float.parseFloat(data.getPlace_longitude());

            LatLng latLng = new LatLng(place_latitude, place_longitude);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(place_name);
            map.addMarker(options);

            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }


        private void bindView(int position){
            final ModelPosts posts = postsList.get(position);

            layout.setTag(this);

            mapView.setTag(posts);
            setMapLocation();

            String pack_id = posts.getPack_id();
            String date = posts.getDate();
            String time = posts.getTime();
            String post_id = posts.getPost_id();
            String timestamp = posts.getTimestamp();
            String like_count = posts.getLike_count();
            final int likes = Integer.parseInt(posts.getLike_count());
            final String postId = posts.getPost_id();

            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timestamp));
            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

            mRowPostDate.setText(date);
            mRowPostTime.setText(time);
            mRowTimePosted.setText(dateTime);
            mLikesCount.setText(like_count);
        }
    }
}



