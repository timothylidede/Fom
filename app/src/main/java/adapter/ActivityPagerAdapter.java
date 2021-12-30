package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import fragment.activity.MeFragment;
import fragment.activity.PackFragment;

public class ActivityPagerAdapter extends FragmentPagerAdapter {
    public ActivityPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new MeFragment();
            case 1:
                return new PackFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
