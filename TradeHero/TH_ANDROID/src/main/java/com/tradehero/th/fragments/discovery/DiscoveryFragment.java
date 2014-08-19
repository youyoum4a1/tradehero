package com.tradehero.th.fragments.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

public class DiscoveryFragment extends DashboardFragment
        implements ActionBar.TabListener
{
    private DiscoverySessionPagerAdapter mDiscoverySessionPagerAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.discovery_fragment, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(view);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        mDiscoverySessionPagerAdapter = new DiscoverySessionPagerAdapter(getFragmentManager());
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Specify that we will be displaying tabs in the action bar.
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            for (int i = 0; i < mDiscoverySessionPagerAdapter.getCount(); ++i)
            {
                actionBar.addTab(
                        actionBar.newTab()
                        .setText(mDiscoverySessionPagerAdapter.getPageTitle(i))
                        .setTabListener(this));
            }
        }
    }

    private class DiscoverySessionPagerAdapter extends FragmentPagerAdapter
    {
        public DiscoverySessionPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            DiscoveryTabType tabType = DiscoveryTabType.values()[position];
            switch (tabType)
            {
                case WHAT_HOT:
                    return new WhatsHotFragment();

                case NEWS:
                    return new FeaturedNewsHeadlineFragment();

                case ACTIVITY:
                    return new DiscoveryActivityFragment();
            }

            throw new IllegalStateException();
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(DiscoveryTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return DiscoveryTabType.values().length;
        }
    }

    //region TabListener
    @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {

    }

    @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {

    }

    @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {

    }
    //endregion
}
