package com.tradehero.th.fragments.discovery;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.astuetz.PagerSlidingTabStrip;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.DiscussionEditPostFragment;
import dagger.Lazy;
import javax.inject.Inject;

public class DiscoveryMainFragment extends DashboardFragment
{
    @Inject Lazy<DashboardNavigator> navigator;

    private DiscoveryPagerAdapter discoveryPagerAdapter;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) PagerSlidingTabStrip pagerSlidingTabStrip;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discoveryPagerAdapter = new DiscoveryPagerAdapter(this.getChildFragmentManager());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_main_fragment, container, false);
        ButterKnife.inject(this, view);
        initViews();
        return view;
    }

    private void initViews()
    {
        tabViewPager.setAdapter(discoveryPagerAdapter);
        pagerSlidingTabStrip.setViewPager(tabViewPager);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(R.string.discovery);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.discussion_edit_post)
        {
            navigator.get().pushFragment(DiscussionEditPostFragment.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView()
    {
        tabViewPager.setAdapter(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.discoveryPagerAdapter = null;
        super.onDestroy();
    }

    private class DiscoveryPagerAdapter extends FragmentPagerAdapter
    {
        public DiscoveryPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            DiscoveryTabType tabType = DiscoveryTabType.values()[position];
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            return Fragment.instantiate(getActivity(), tabType.fragmentClass.getName(), args);
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
}
