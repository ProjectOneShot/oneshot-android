package application.oneshot.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragmentPagerAdapter
        extends FragmentPagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();

    public OnboardingFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(Fragment fragment) {
        mFragments.add(fragment);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
}
