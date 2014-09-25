package com.tradehero.th.fragments.chinabuild;

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
import com.tradehero.th.R;
import com.tradehero.th.fragments.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.search.SearchFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfChinaConceptFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfHotHoldFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfHotWatchFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfMineFragment;
import com.viewpagerindicator.TabPageIndicator;
import timber.log.Timber;

public class MainTabFragmentTrade extends AbsBaseFragment
{

    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    @InjectView(R.id.imgSearch) ImageView imgSearch;
    FragmentPagerAdapter adapter;

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
    }

    @OnClick(R.id.imgSearch)
    public void onSearchClicked()
    {
        Timber.d("Search Button Clicked!");
        gotoDashboard(SearchFragment.class.getName());
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

    private static final String[] CONTENT = new String[] {"我的交易", "热门关注", "热门持有", "中国概念"};

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
                    return new TradeOfHotWatchFragment();

                case 2:
                    return new TradeOfHotHoldFragment();

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
}
