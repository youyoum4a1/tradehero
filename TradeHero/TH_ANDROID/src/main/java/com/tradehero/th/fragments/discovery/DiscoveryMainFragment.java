package com.tradehero.th.fragments.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

public class DiscoveryMainFragment extends DashboardFragment
        implements ActionBar.TabListener
{
    private DiscoverySessionPagerAdapter mDiscoverySessionPagerAdapter;
    @InjectView(R.id.pager) ViewPager mViewPager;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_main_fragment, container, false);
        initView(view);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            setupTabs(actionBar);
        }

        setActionBarTitle(getString(R.string.discovery));
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            mDiscoverySessionPagerAdapter = new DiscoverySessionPagerAdapter(((Fragment)this).getChildFragmentManager());
            setupPager(actionBar);
        }
    }

    private void setupTabs(ActionBar actionBar)
    {
        actionBar.removeAllTabs();
        for (int i = 0; i < mDiscoverySessionPagerAdapter.getCount(); ++i)
        {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mDiscoverySessionPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        actionBar.selectTab(actionBar.getTabAt(actionBar.getTabCount() / 2));
        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    private void setupPager(final ActionBar actionBar)
    {
        mViewPager.setAdapter(mDiscoverySessionPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
            }
        });
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
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {

    }

    @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {

    }
    //endregion
}
