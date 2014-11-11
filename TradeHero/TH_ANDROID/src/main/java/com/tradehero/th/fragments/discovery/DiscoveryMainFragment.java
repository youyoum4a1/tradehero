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
import com.astuetz.PagerSlidingTabStrip;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.DiscussionEditPostFragment;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SingleAttributeEvent;
import javax.inject.Inject;
import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Lazy;

public class DiscoveryMainFragment extends DashboardFragment
{
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject Analytics analytics;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) PagerSlidingTabStrip pagerSlidingTabStrip;

    private DiscoveryPagerAdapter discoveryPagerAdapter;
    private long beginTime;
    private int oldPageItem;

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
        beginTime = System.currentTimeMillis();
        oldPageItem = 0;
        tabViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                reportAnalytics();
                beginTime = System.currentTimeMillis();
                oldPageItem = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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
        reportAnalytics();
        tabViewPager.setAdapter(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void reportAnalytics()
    {
        long duration = (System.currentTimeMillis()-beginTime)/1000;
        String s = AnalyticsConstants.Time10M;
        if (duration <= 10)
        {
            s = AnalyticsConstants.Time1T10S;
        }
        else if (duration <= 30)
        {
            s = AnalyticsConstants.Time11T30S;
        }
        else if (duration <= 60)
        {
            s = AnalyticsConstants.Time31T60S;
        }
        else if (duration <= 180)
        {
            s = AnalyticsConstants.Time1T3M;
        }
        else if (duration <= 600)
        {
            s = AnalyticsConstants.Time3T10M;
        }
        if (oldPageItem == 0){
            analytics.fireEvent(new SingleAttributeEvent(AnalyticsConstants.DiscoverNewsViewed,
                    AnalyticsConstants.TimeOnScreen, s));
        }
        else if (oldPageItem == 1)
        {
            analytics.fireEvent(new SingleAttributeEvent(AnalyticsConstants.DiscoverDiscussionsViewed,
                    AnalyticsConstants.TimeOnScreen, s));
        }
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
