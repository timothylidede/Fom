package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import fragment.connections.AddFragment;
import fragment.connections.LocationFragment;

public class ConnectionsPagerAdapter extends FragmentPagerAdapter {

    public ConnectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new AddFragment();
            case 1:
                return new LocationFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
