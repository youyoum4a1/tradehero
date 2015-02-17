package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import javax.inject.Inject;

public class LeaderboardMarkUserPagerFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_LEADERBOARD_ID = LeaderboardMarkUserPagerFragment.class.getName() + ".leaderboardId";

    @Inject Context context;
    @InjectView(R.id.android_tabs) SlidingTabLayout pagerSlidingTabLayout;
    @InjectView(R.id.pager) ViewPager viewPager;
    private LeaderboardPagerAdapter leaderboardPagerAdapter;
    @NonNull protected LeaderboardDefKey leaderboardDefKey;

    public static void putLeaderboardDefKey(@NonNull Bundle args, @NonNull LeaderboardDefKey leaderboardDefKey)
    {
        args.putInt(BUNDLE_KEY_LEADERBOARD_ID, leaderboardDefKey.key);
    }

    @NonNull public static LeaderboardDefKey getLeadboardDefKey(@NonNull Bundle args)
    {
        return new LeaderboardDefKey(args.getInt(BUNDLE_KEY_LEADERBOARD_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardDefKey = getLeadboardDefKey(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_tabbed, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initViews(view);
    }

    private void initViews(View view)
    {
        leaderboardPagerAdapter = new LeaderboardPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(leaderboardPagerAdapter);
        pagerSlidingTabLayout.setCustomTabView(R.layout.th_tab_indicator, android.R.id.title);
        pagerSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabLayout.setViewPager(viewPager);
    }

    @Override public void onDestroyView()
    {
        leaderboardPagerAdapter = null;
        super.onDestroyView();
    }

    private class LeaderboardPagerAdapter extends FragmentPagerAdapter
    {
        public LeaderboardPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle(getArguments());
            LeaderboardMarkUserListFragment.putLeaderboardDefKey(args, leaderboardDefKey);
            LeaderboardMarkUserListFragment.putLeaderboardType(args, LeaderboardType.values()[position]);
            Fragment f = new LeaderboardMarkUserListFragment();
            f.setArguments(args);
            return f;
        }

        @Override public int getCount()
        {
            return LeaderboardType.values().length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(LeaderboardType.values()[position].getTitleResId());
        }
    }
}
