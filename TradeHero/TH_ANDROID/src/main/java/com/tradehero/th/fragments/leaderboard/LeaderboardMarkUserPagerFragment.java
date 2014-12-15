package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
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
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import java.util.List;
import javax.inject.Inject;

public class LeaderboardMarkUserPagerFragment extends LeaderboardDefFragment
{
    @Inject Context context;
    @InjectView(R.id.android_tabs) SlidingTabLayout pagerSlidingTabLayout;
    @InjectView(R.id.pager) ViewPager viewPager;
    private LeaderboardPagerAdapter leaderboardPagerAdapter;

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
        pagerSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_blue));
    }

    private void refreshViewpager()
    {
        pagerSlidingTabLayout.setViewPager(viewPager);
    }

    @Override public void onDestroyView()
    {
        leaderboardPagerAdapter = null;
        super.onDestroyView();
    }

    @Override protected void onLeaderboardDefListLoaded(List<LeaderboardDefDTO> leaderboardDefDTOs)
    {
        leaderboardPagerAdapter.setItems(leaderboardDefDTOs);
    }

    private class LeaderboardPagerAdapter extends FragmentPagerAdapter
    {
        List<LeaderboardDefDTO> leaderboardDefDTOs = new LeaderboardDefDTOList();

        public LeaderboardPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        public void setItems(List<LeaderboardDefDTO> items)
        {
            leaderboardDefDTOs.clear();
            leaderboardDefDTOs.addAll(items);
            notifyDataSetChanged();
            refreshViewpager();
        }

        @Override public Fragment getItem(int position)
        {
            LeaderboardDefDTO dto = leaderboardDefDTOs.get(position);
            Bundle args = getArguments();
            if (args == null)
            {
                args = new Bundle();
            }
            LeaderboardMarkUserListFragment.putLeaderboardDefKey(args, dto.getLeaderboardDefKey());
            return Fragment.instantiate(getActivity(), LeaderboardMarkUserListFragment.class.getName(), args);
        }

        @Override public int getCount()
        {
            return leaderboardDefDTOs.size();
        }

        @Override public CharSequence getPageTitle(int position)
        {
            //TODO
            return getString(R.string.leaderboard_type_stocks);
        }
    }
}
