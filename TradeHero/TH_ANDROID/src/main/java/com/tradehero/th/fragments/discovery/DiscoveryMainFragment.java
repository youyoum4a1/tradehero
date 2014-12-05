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
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.AnalyticsDuration;
import com.tradehero.th.utils.metrics.events.SingleAttributeEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.THTabView;
import javax.inject.Inject;

@Routable({"news", "discussion", "academy"})
public class DiscoveryMainFragment extends DashboardFragment
{
    @Inject Analytics analytics;
    @Inject THRouter thRouter;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    private DiscoveryPagerAdapter discoveryPagerAdapter;
    private long beginTime;
    private int oldPageItem;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discoveryPagerAdapter = new DiscoveryPagerAdapter(this.getChildFragmentManager());
        thRouter.inject(this);
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
        if (!Constants.RELEASE)
        {
            tabViewPager.setOffscreenPageLimit(0);
        }

        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_blue));
        pagerSlidingTabStrip.setViewPager(tabViewPager);

        displayNewIcon();

        beginTime = System.currentTimeMillis();
        oldPageItem = 0;
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int i, float v, int i2)
            {
            }

            @Override
            public void onPageSelected(int i)
            {
                reportAnalytics();
                beginTime = System.currentTimeMillis();
                oldPageItem = i;
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {
            }
        });
    }

    private void displayNewIcon()
    {
        for (int i = 0; i < discoveryPagerAdapter.getCount(); i++)
        {
            if (discoveryPagerAdapter.isNew(i))
            {
                THTabView tabView = (THTabView) pagerSlidingTabStrip.getTabStrip().getChildAt(i);
                tabView.setIcon(R.drawable.icn_new_discover);
            }
        }
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

    @Override public void onDestroyView()
    {
        reportAnalytics();
        tabViewPager.setAdapter(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void reportAnalytics()
    {
        AnalyticsDuration duration = AnalyticsDuration.sinceTimeMillis(beginTime);
        if (oldPageItem == 0)
        {
            analytics.fireEvent(new SingleAttributeEvent(
                    AnalyticsConstants.DiscoverNewsViewed,
                    AnalyticsConstants.TimeOnScreen,
                    duration.toString()));
        }
        else if (oldPageItem == 1)
        {
            analytics.fireEvent(new SingleAttributeEvent(
                    AnalyticsConstants.DiscoverDiscussionsViewed,
                    AnalyticsConstants.TimeOnScreen,
                    duration.toString()));
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

        public boolean isNew(int position)
        {
            return DiscoveryTabType.values()[position].isNew;
        }
    }
}
