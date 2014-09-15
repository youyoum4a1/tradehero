package com.tradehero.th.fragments.chinabuild;

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
import com.tradehero.th2.R;
import com.tradehero.th.fragments.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.DiscoveryHotTopicFragment;
import com.tradehero.th.fragments.chinabuild.fragment.DiscoveryRecentNewsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.DiscoveryStockGodNewsFragment;
import com.viewpagerindicator.TabPageIndicator;

public class MainTabFragmentDiscovery extends AbsBaseFragment
{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    private FragmentPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


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

    private static final String[] CONTENT = new String[] {"最新动态", "热门话题", "股神动态"};

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
                    return new DiscoveryHotTopicFragment();

                case 2:
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

}
