package com.tradehero.th.fragments.discovery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.android.common.SlidingTabLayout;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.AnalyticsDuration;
import com.tradehero.th.utils.metrics.events.SingleAttributeEvent;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;

@Routable(DiscoveryMainFragment.ROUTER_DISCOVERY_TAB_INDEX + ":tabIndex")
public class DiscoveryMainFragment extends DashboardFragment
{
    public static final String ROUTER_DISCOVERY_TAB_INDEX = "discovery/tab-index/";

    @Inject Analytics analytics;
    @Inject THRouter thRouter;
    @Bind(R.id.pager) ViewPager tabViewPager;
    @Bind(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    @RouteProperty("tabIndex") Integer tabIndex;
    private DiscoveryPagerAdapter discoveryPagerAdapter;
    private long beginTime;
    private int oldPageItem;

    private BaseLiveFragmentUtil liveFragmentUtil;

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("discovery-news", ROUTER_DISCOVERY_TAB_INDEX + DiscoveryTabType.NEWSFEED.ordinal());
        router.registerAlias("discovery-discussion", ROUTER_DISCOVERY_TAB_INDEX + DiscoveryTabType.DISCUSSION.ordinal());
        router.registerAlias("discovery-academy", ROUTER_DISCOVERY_TAB_INDEX + DiscoveryTabType.ACADEMY.ordinal());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discoveryPagerAdapter = new DiscoveryPagerAdapter(this.getChildFragmentManager());
        thRouter.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.discovery_main_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tabViewPager.setAdapter(discoveryPagerAdapter);
        if (!Constants.RELEASE)
        {
            tabViewPager.setOffscreenPageLimit(0);
        }

        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setDistributeEvenly(true);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);

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
        if (tabIndex != null)
        {
            tabViewPager.setCurrentItem(tabIndex);
            tabIndex = null;
        }
        liveFragmentUtil = BaseLiveFragmentUtil.createFor(this, view);
    }

    @Override public void onResume()
    {
        super.onResume();
        liveFragmentUtil.onResume();
    }

    @Override public void onLiveTradingChanged(boolean isLive)
    {
        super.onLiveTradingChanged(isLive);
        liveFragmentUtil.setCallToAction(isLive);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.discovery);
    }

    @Override public void onDestroyView()
    {
        liveFragmentUtil.onDestroyView();
        liveFragmentUtil = null;
        reportAnalytics();
        tabViewPager.setAdapter(null);
        ButterKnife.unbind(this);
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
    }
}
