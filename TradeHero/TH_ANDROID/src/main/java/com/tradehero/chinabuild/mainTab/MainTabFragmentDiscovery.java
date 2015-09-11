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
import com.tradehero.chinabuild.fragment.discovery.DiscoverySquareFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryStockGodNewsFragment;
import com.tradehero.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.viewpagerindicator.TabPageIndicator;
import javax.inject.Inject;


public class MainTabFragmentDiscovery extends AbsBaseFragment implements ViewPager.OnPageChangeListener
{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;

    @Inject Analytics analytics;
    private FragmentPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_tab_fragment_discovery_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        adapter = new CustomAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        indicator.setViewPager(pager);
    }

    private static final String[] CONTENT = new String[] {"广场", "股神动态"};

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if(i == 0){
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_DISCOVERY_SQUARE));
            return;
        }
        if(i == 1){
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_DISCOVERY_GOD));
            return;
        }
    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

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
                    return new DiscoverySquareFragment();
                case 1:
                    return new DiscoveryStockGodNewsFragment();
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

    @OnClick(R.id.tvCreateTimeLine)
    public void createTimeLine()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(DiscussSendFragment.BUNDLE_KEY_REWARD, true);
        gotoDashboard(DiscoveryDiscussSendFragment.class.getName(), bundle);
    }

}
