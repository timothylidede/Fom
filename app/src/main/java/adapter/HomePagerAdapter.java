package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import fragment.ActivityFragment;
import fragment.ConnectionsFragment;
import fragment.HomeFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {
    public HomePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return ActivityFragment.create();
            case 1:
                return HomeFragment.create();
            case 2:
                return ConnectionsFragment.create();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
