package com.tradehero.chinabuild;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.search.SearchUniteFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfChinaConceptFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfHotHoldFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfMineFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfRisePercentFragment;
import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.viewpagerindicator.TabPageIndicator;

import javax.inject.Inject;

public class MainTabFragmentTrade extends AbsBaseFragment implements ViewPager.OnPageChangeListener
{

    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    @InjectView(R.id.imgSearch) ImageView imgSearch;
    private FragmentPagerAdapter adapter;

    @Inject Analytics analytics;

    public static final String TAG = "main_tab_fragment_trade";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

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
        if(!THSharePreferenceManager.isShowTradeHoldOnce(getActivity()))
        {
            pager.setCurrentItem(1);
        }
    }

    @OnClick(R.id.imgSearch)
    public void onSearchClicked()
    {
        analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_SEARCH));
        gotoDashboard(SearchUniteFragment.class.getName());
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    private static final String[] CONTENT = new String[] {"我的交易", "热门持有", "涨幅榜单", "中国概念"};

    @Override public void onPageScrolled(int i, float v, int i2)
    {
    }



    @Override public void onPageSelected(int i)
    {
        if (i == 0)
        {
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_MINE_TRADE));
        }
        else if (i == 1)
        {
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_HOLD));
        }
        else if (i == 2)
        {
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_CHINA));
        }
        else if (i == 3)
        {
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.TRADE_PAGE_RISE));
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
