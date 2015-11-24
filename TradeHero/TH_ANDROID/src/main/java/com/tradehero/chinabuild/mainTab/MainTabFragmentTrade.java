package com.tradehero.chinabuild.mainTab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfChinaConceptFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfHotHoldFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfMineFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfRisePercentFragment;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.viewpagerindicator.TabPageIndicator;
import javax.inject.Inject;

public class MainTabFragmentTrade extends AbsBaseFragment implements ViewPager.OnPageChangeListener
{

    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;

    @Inject Analytics analytics;
    private FragmentPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_tab_fragment_trade_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        adapter = new CustomAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5);
        indicator.setViewPager(pager);
        indicator.setOutsideListener(this);
    }

    @OnClick(R.id.imgSearch)
    public void onSearchClicked()
    {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_SEARCH));
        gotoDashboard(SearchUnitFragment.class.getName(), new Bundle());
    }

    private static final String[] CONTENT = new String[] {"我的交易", "热门持有", "涨幅榜单", "中国概念"};

    @Override public void onPageScrolled(int i, float v, int i2)
    {
    }

    @Override public void onPageSelected(int i)
    {
        if (i == 0)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_MINE_TRADE));
        }
        else if (i == 1)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_HOLD));
        }
        else if (i == 2)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_CHINA));
        }
        else if (i == 3)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_RISE));
        }
    }

    @Override public void onPageScrollStateChanged(int i)
    {
    }

    class CustomAdapter extends FragmentPagerAdapter
    {
        public CustomAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new TradeOfMineFragment();
                case 1:
                    return new TradeOfHotHoldFragment();
                case 2:
                    return new TradeOfRisePercentFragment();
                case 3:
                    return new TradeOfChinaConceptFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount()
        {
            return CONTENT.length;
        }
    }

    public int getCurrentFragmentItem(){
        if(pager == null){
            return -1;
        }
        return pager.getCurrentItem();
    }
}
