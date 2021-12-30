package fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savala.fom.Potentials;
import com.savala.fom.R;
import com.savala.fom.Settings;

import adapter.ActivityPagerAdapter;
import view.ActivityTopTabsView;

public class ActivityFragment extends BaseFragment {
    private ImageView have_potentials;
    private ImageView have_potentials_circle;
    private ImageView potentials;
    private ImageView mSettings;

    public static ActivityFragment create(){
         return new ActivityFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_activity;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.view_pager_activity);
        ActivityPagerAdapter adapter = new ActivityPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        RelativeLayout layout = (RelativeLayout) root.findViewById(R.id.view_activity);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        ActivityTopTabsView activityTopTabsView = (ActivityTopTabsView) root.findViewById(R.id.top_tab);
        activityTopTabsView.setUpWithViewPager(viewPager);

        potentials = (ImageView) root.findViewById(R.id.potentials);
        have_potentials_circle = (ImageView) root.findViewById(R.id.have_potentials_circle);
        have_potentials = (ImageView) root.findViewById(R.id.have_potentials);
        mSettings = (ImageView) root.findViewById(R.id.settings);

        FirebaseDatabase.getInstance().getReference("potentials")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            potentials.setVisibility(View.INVISIBLE);
                            have_potentials.setVisibility(View.VISIBLE);
                            have_potentials_circle.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Settings.class);
                startActivity(intent);
            }
        });
        potentials.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activity4Intent = new Intent(getContext(), Potentials.class);
                startActivity(activity4Intent);
            }

        });
        have_potentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Potentials.class);
                startActivity(intent);
            }
        });
        have_potentials_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Potentials.class);
                startActivity(intent);
            }
        });
    }
}
