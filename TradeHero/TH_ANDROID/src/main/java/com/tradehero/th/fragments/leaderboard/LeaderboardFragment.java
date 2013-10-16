package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefMostSkilledListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefSectorListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefTimePeriodListKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import java.util.List;
import javax.inject.Inject;

public class LeaderboardFragment extends DashboardFragment implements DTOCache.Listener<LeaderboardDefListKey, List<LeaderboardDefKey>>
{
    private static final String TAG = LeaderboardFragment.class.getName();

    @Inject protected LeaderboardDefListCache leaderboardDefListCache;
    @Inject protected LeaderboardDefCache leaderboardDefCache;
    @Inject protected ProviderListCache providerCache;

    private View view;
    private ListAdapter mostSkilledListAdapter;
    private ListAdapter timePeriodListAdapter;
    private ListAdapter sectorListViewAdapter;
    private boolean fetched = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.leaderboard_screen, container, false);

        prepareAdapters();

        initViews();

        return view;
    }

    private void prepareAdapters()
    {
        leaderboardDefListCache.getOrFetch(new LeaderboardDefMostSkilledListKey(), false, this).execute();
    }

    private void initViews()
    {
        // section for Most Skilled
        //
        //mostSkilledListView.setAdapter();
        //
        //// section for ranking by time period
        //
        //// section for ranking by exchange, sector
        //ListView roiListView = (ListView) view.findViewById(R.id.leaderboard_roi);
        //roiListView.setAdapter(createMostSkilledListViewAdapter());
    }

    @Override public void onStop()
    {
        super.onStop();
        fetched = false;
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.leaderboard_menu, menu);
        getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.leaderboards));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO switch sorting type for leaderboard
        switch (item.getItemId())
        {
            case R.id.leaderboard_sort_by_hero_quotient:
                break;
            case R.id.leaderboard_sort_by_roi:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void onDTOReceived(LeaderboardDefListKey key, List<LeaderboardDefKey> value)
    {
        if (!fetched)
        {
            fetched = true;
            leaderboardDefListCache.getOrFetch(new LeaderboardDefTimePeriodListKey(), false, this).execute();
            leaderboardDefListCache.getOrFetch(new LeaderboardDefSectorListKey(), false, this).execute();
        }

        if (key instanceof LeaderboardDefMostSkilledListKey)
        {
            ListView mostSkilledListView = (ListView) view.findViewById(R.id.leaderboard_most_skilled);
            mostSkilledListView.setAdapter(createMostSkilledListAdapter(value));
        }
        else if (key instanceof LeaderboardDefTimePeriodListKey)
        {
            ListView timePeriodListView = (ListView) view.findViewById(R.id.leaderboard_time_period);
            timePeriodListView.setAdapter(createTimePeriodListAdapter(value));
        }
        else if (key instanceof LeaderboardDefSectorListKey)
        {
            ListView sectorListView = (ListView) view.findViewById(R.id.leaderboard_sector);
            sectorListView.setAdapter(createTimeSectorAdapter(value));
        }
    }

    private ListAdapter createTimeSectorAdapter(List<LeaderboardDefKey> keys)
    {
        return new LeaderboardDefListAdapter(
            getActivity(), getActivity().getLayoutInflater(), leaderboardDefCache.getOrFetch(keys), R.layout.leaderboard_def_item);
    }

    private ListAdapter createTimePeriodListAdapter(List<LeaderboardDefKey> keys)
    {
        return new LeaderboardDefTimePeriodListAdapter(
                getActivity(), getActivity().getLayoutInflater(), leaderboardDefCache.getOrFetch(keys), R.layout.leaderboard_def_item);
    }

    private ListAdapter createMostSkilledListAdapter(List<LeaderboardDefKey> keys)
    {
        return new LeaderboardDefMostSkilledListAdapter(
                getActivity(), getActivity().getLayoutInflater(), leaderboardDefCache.getOrFetch(keys), R.layout.leaderboard_def_item);
    }
}
