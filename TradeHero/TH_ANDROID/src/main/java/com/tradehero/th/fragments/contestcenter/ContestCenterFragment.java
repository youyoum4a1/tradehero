package com.tradehero.th.fragments.contestcenter;

import android.content.Context;
import android.os.Bundle;
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
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.base.DashboardFragment;
import javax.inject.Inject;

@Deprecated
public class ContestCenterFragment extends DashboardFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Bind(R.id.android_tabs) SlidingTabLayout pagerSlidingTabLayout;
    @Bind(R.id.pager) ViewPager viewPager;

    private BaseLiveFragmentUtil liveFragmentUtil;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.dashboard_contest_center);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contest_center, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
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

    @Override public void onDestroyView()
    {
        liveFragmentUtil.onDestroyView();
        liveFragmentUtil = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void initViews()
    {
        ContestCenterPagerAdapter adapter = new ContestCenterPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        pagerSlidingTabLayout.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabLayout.setDistributeEvenly(true);
        pagerSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabLayout.setViewPager(viewPager);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        liveFragmentUtil = BaseLiveFragmentUtil.createFor(this, view);
    }

    private class ContestCenterPagerAdapter extends FragmentPagerAdapter
    {
        public ContestCenterPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int position)
        {
            ContestCenterTabType tabType = ContestCenterTabType.values()[position];
            Bundle args = getArguments();
            if (args == null)
            {
                args = new Bundle();
            }
            return Fragment.instantiate(getActivity(), tabType.tabClass.getName(), args);
        }

        @Override public int getCount()
        {
            return ContestCenterTabType.values().length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(ContestCenterTabType.values()[position].titleRes);
        }
    }
}
