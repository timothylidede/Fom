
package fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.savala.fom.CreatePack;
import com.savala.fom.EditProfileActivity;
import com.savala.fom.R;

import adapter.ConnectionsPagerAdapter;
import view.ConnectionsTopTabsView;

public class ConnectionsFragment extends BaseFragment {
    private ImageView mCreatePack;
    private ImageView mViewMyself;

    public static ConnectionsFragment create(){
        return new ConnectionsFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_connections;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.view_pager_connections);
        ConnectionsPagerAdapter adapter = new ConnectionsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        final View background = root.findViewById(R.id.am_connections_view);
        mCreatePack = (ImageView) root.findViewById(R.id.create_pack);
        mViewMyself = (ImageView) root.findViewById(R.id.view_myself);

        RelativeLayout layout = (RelativeLayout) root.findViewById(R.id.view_connect);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        ConnectionsTopTabsView activityTopTabsView = (ConnectionsTopTabsView) root.findViewById(R.id.top_tab);
        activityTopTabsView.setUpWithViewPager(viewPager);

        mCreatePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreatePack.class);
                startActivity(intent);
            }
        });

        mViewMyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
