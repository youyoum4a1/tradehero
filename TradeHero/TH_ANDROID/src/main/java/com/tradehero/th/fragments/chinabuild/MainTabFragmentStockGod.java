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
import com.tradehero.th.fragments.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard.StockGodListBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard.StockGodListMoreFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th2.R;
import com.viewpagerindicator.TabPageIndicator;
import timber.log.Timber;

public class MainTabFragmentStockGod extends AbsBaseFragment
{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    FragmentPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_tab_fragment_stockgod_layout, container, false);
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
        Timber.d("onResume MainTabFragmentStockGod!");
    }

    private static final String[] CONTENT = new String[] {"推荐榜", "人气榜", "土豪榜", "更多榜单"};

    class CustomAdapter extends FragmentPagerAdapter
    {
        public CustomAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            StockGodListBaseFragment fragment;
            Bundle args = new Bundle();
            switch (position)
            {
                case 0:
                    fragment = new StockGodListBaseFragment();
                    args.putInt(StockGodListBaseFragment.BUNLDE_LEADERBOARD_KEY, LeaderboardDefKeyKnowledge.DAYS_ROI);
                    fragment.setArguments(args);
                    return fragment;
                case 1:
                    fragment = new StockGodListBaseFragment();
                    args.putInt(StockGodListBaseFragment.BUNLDE_LEADERBOARD_KEY, LeaderboardDefKeyKnowledge.POPULAR);
                    fragment.setArguments(args);
                    return fragment;
                case 2:
                    fragment = new StockGodListBaseFragment();
                    args.putInt(StockGodListBaseFragment.BUNLDE_LEADERBOARD_KEY, LeaderboardDefKeyKnowledge.WEALTH);
                    fragment.setArguments(args);
                    return fragment;
                case 3:
                    return new StockGodListMoreFragment();
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
