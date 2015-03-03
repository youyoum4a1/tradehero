package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;

@Routable("securityFx/:securityRawInfo")
public class FXMainFragment extends DashboardFragment
{
    //@Inject Analytics analytics;
    @Inject THRouter thRouter;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    private DiscoveryPagerAdapter discoveryPagerAdapter;
    //private long beginTime;
    //private int oldPageItem;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discoveryPagerAdapter = new DiscoveryPagerAdapter(this.getChildFragmentManager());
        discoveryPagerAdapter.setBundle(getArguments());
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
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);

        //displayNewIcon();

        //beginTime = System.currentTimeMillis();
        //oldPageItem = 0;
        //pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        //{
        //    @Override
        //    public void onPageScrolled(int i, float v, int i2)
        //    {
        //    }
        //
        //    @Override
        //    public void onPageSelected(int i)
        //    {
        //        reportAnalytics();
        //        beginTime = System.currentTimeMillis();
        //        oldPageItem = i;
        //    }
        //
        //    @Override
        //    public void onPageScrollStateChanged(int i)
        //    {
        //    }
        //});
    }

    //private void displayNewIcon()
    //{
    //    for (int i = 0; i < discoveryPagerAdapter.getCount(); i++)
    //    {
    //        if (discoveryPagerAdapter.isNew(i))
    //        {
    //            THTabView tabView = (THTabView) pagerSlidingTabStrip.getTabStrip().getChildAt(i);
    //            tabView.setIcon(R.drawable.icn_new_discover);
    //        }
    //    }
    //}

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.fx);
    }

    @Override public void onDestroyView()
    {
        //reportAnalytics();
        tabViewPager.setAdapter(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    //private void reportAnalytics()
    //{
    //    AnalyticsDuration duration = AnalyticsDuration.sinceTimeMillis(beginTime);
    //    if (oldPageItem == 0)
    //    {
    //        analytics.fireEvent(new SingleAttributeEvent(
    //                AnalyticsConstants.DiscoverNewsViewed,
    //                AnalyticsConstants.TimeOnScreen,
    //                duration.toString()));
    //    }
    //    else if (oldPageItem == 1)
    //    {
    //        analytics.fireEvent(new SingleAttributeEvent(
    //                AnalyticsConstants.DiscoverDiscussionsViewed,
    //                AnalyticsConstants.TimeOnScreen,
    //                duration.toString()));
    //    }
    //}

    @Override public void onDestroy()
    {
        this.discoveryPagerAdapter = null;
        super.onDestroy();
    }

    private class DiscoveryPagerAdapter extends FragmentPagerAdapter
    {
        private Bundle mArgs;

        public DiscoveryPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            FXMainTabType tabType = FXMainTabType.values()[position];
            ActionBarOwnerMixin.putKeyShowHomeAsUp(mArgs, true);
            return Fragment.instantiate(getActivity(), tabType.fragmentClass.getName(), mArgs);
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(FXMainTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return FXMainTabType.values().length;
        }

        //public boolean isNew(int position)
        //{
        //    return FXMainTabType.values()[position].isNew;
        //}

        public void setBundle(Bundle args)
        {
            mArgs = args;
        }
    }
}
