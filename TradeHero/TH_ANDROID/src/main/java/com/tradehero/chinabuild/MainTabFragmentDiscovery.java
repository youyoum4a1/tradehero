package com.tradehero.chinabuild;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryNewsFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryRecentNewsFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryRewardFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryStockGodNewsFragment;
import com.tradehero.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.viewpagerindicator.TabPageIndicator;

import javax.inject.Inject;


public class MainTabFragmentDiscovery extends AbsBaseFragment implements ViewPager.OnPageChangeListener
{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    private FragmentPagerAdapter adapter;

    @InjectView(R.id.tvCreateTimeLine) TextView tvCreateTimeLine;

    @Inject Analytics analytics;

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
        pager.setOffscreenPageLimit(5);
        indicator.setViewPager(pager);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private static final String[] CONTENT = new String[] {"最新动态"
              , "资讯","悬赏帖", "股神动态"
    };

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if(i == 0){
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_DISCOVERY_LATEST));
            return;
        }
        else if(i == 1){
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_DISCOVERY_HOT));
            return;
        }
        else if(i == 2){
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_DISCOVERY_REWARD));
            return;
        }
        else if(i == 3){
            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_DISCOVERY_GOD));
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
                    return new DiscoveryRecentNewsFragment();

                case 1:
                    return new DiscoveryNewsFragment();

                case 2:
                    return new DiscoveryRewardFragment();

                case 3:
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
