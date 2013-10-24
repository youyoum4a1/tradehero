package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefExchangeListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefMostSkilledListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefSectorListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefTimePeriodListKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class LeaderboardFragment extends DashboardFragment implements DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final String TAG = LeaderboardFragment.class.getName();

    @Inject protected LeaderboardDefListCache leaderboardDefListCache;
    @Inject protected LeaderboardDefCache leaderboardDefCache;
    @Inject protected ProviderListCache providerCache;

    private View view;
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
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(getString(R.string.leaderboards));
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

    @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        if (!fetched)
        {
            fetched = true;
            leaderboardDefListCache.getOrFetch(new LeaderboardDefTimePeriodListKey(), false, this).execute();
            leaderboardDefListCache.getOrFetch(new LeaderboardDefSectorListKey(), false, this).execute();
            leaderboardDefListCache.getOrFetch(new LeaderboardDefExchangeListKey(), false, this).execute();
        }

        if (value != null)
        {
            if (key instanceof LeaderboardDefMostSkilledListKey)
            {
                ListView mostSkilledListView = (ListView) view.findViewById(R.id.leaderboard_most_skilled);
                mostSkilledListView.setAdapter(createMostSkilledListAdapter(value));
                mostSkilledListView.setOnItemClickListener(createLeaderboardItemClickListener());
            }
            else if (key instanceof LeaderboardDefTimePeriodListKey)
            {
                ListView timePeriodListView = (ListView) view.findViewById(R.id.leaderboard_time_period);
                timePeriodListView.setAdapter(createTimePeriodListAdapter(value));
                timePeriodListView.setOnItemClickListener(createLeaderboardItemClickListener());
            }
            else if (key instanceof LeaderboardDefSectorListKey && (value.size() > 0))
            {
                LeaderboardDefDTO sectorDto = initDefaultLeaderboardDefDTOForSector();
                addItemToSectorSection(sectorDto);
            }
            else if (key instanceof LeaderboardDefExchangeListKey)
            {
                LeaderboardDefDTO sectorDto = initDefaultLeaderboardDefDTOForExchange();
                addItemToSectorSection(sectorDto);
            }
        }
    }

    private AdapterView.OnItemClickListener createLeaderboardItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                LeaderboardDefDTO dto = (LeaderboardDefDTO) adapterView.getAdapter().getItem(position);

                Bundle bundle = new Bundle();
                bundle.putInt(LeaderboardDTO.LEADERBOARD_ID, dto.getId());
                navigator.pushFragment(LeaderboardListViewFragment.class, bundle);
            }
        };
    }

    //<editor-fold desc="Init Defaults">
    private LeaderboardDefDTO initDefaultLeaderboardDefDTOForExchange()
    {
        LeaderboardDefDTO dto = new LeaderboardDefDTO();
        dto.id = LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID;
        dto.name = getString(R.string.leaderboard_by_exchange);
        return dto;
    }

    private LeaderboardDefDTO initDefaultLeaderboardDefDTOForSector()
    {
        LeaderboardDefDTO dto = new LeaderboardDefDTO();
        dto.id = LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID;
        dto.name = getString(R.string.leaderboard_by_sector);
        return dto;
    }
    //</editor-fold>

    private void addItemToSectorSection(LeaderboardDefDTO dto)
    {
        ListView sectorListView = (ListView) view.findViewById(R.id.leaderboard_sector);
        if (sectorListView.getAdapter() == null)
        {
            List<LeaderboardDefDTO> sectorDefDTOs = new ArrayList<>();
            sectorListView.setAdapter(
                    new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), sectorDefDTOs, R.layout.leaderboard_def_item));
        }
        LeaderboardDefListAdapter sectorListViewAdapter = (LeaderboardDefListAdapter) sectorListView.getAdapter();
        sectorListViewAdapter.addItem(dto);
        sectorListViewAdapter.notifyDataSetChanged();
        sectorListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                LeaderboardDefDTO dto = (LeaderboardDefDTO) adapterView.getItemAtPosition(position);
                if (dto != null)
                {
                    switch (dto.getId())
                    {
                        case LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID:
                            navigator.pushFragment(LeaderboardDefListViewFragment.class, new LeaderboardDefSectorListKey().getArgs());
                            break;
                        case LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID:
                            navigator.pushFragment(LeaderboardDefListViewFragment.class, new LeaderboardDefExchangeListKey().getArgs());
                            break;
                    }
                }
            }
        });
    }

    private ListAdapter createTimePeriodListAdapter(List<LeaderboardDefKey> keys)
    {
        // sort time period items by number of days
        List<LeaderboardDefDTO> timePeriodItems = leaderboardDefCache.getOrFetch(keys);
        Collections.sort(timePeriodItems, new Comparator<LeaderboardDefDTO>()
        {
            @Override public int compare(LeaderboardDefDTO lhs, LeaderboardDefDTO rhs)
            {
                if (lhs == rhs) return 0;
                else if (lhs == null) return -1;
                else if (rhs == null) return 1;
                else if (lhs.toDateDays == null) return -1;
                else if (rhs.toDateDays == null) return 1;
                else if (lhs.toDateDays.equals(rhs.toDateDays)) return 0;
                else return (lhs.toDateDays > rhs.toDateDays) ? 1 : -1;
            }
        });
        return new LeaderboardDefTimePeriodListAdapter(
                getActivity(), getActivity().getLayoutInflater(), timePeriodItems, R.layout.leaderboard_def_item);
    }

    private ListAdapter createMostSkilledListAdapter(List<LeaderboardDefKey> keys)
    {
        return new LeaderboardDefMostSkilledListAdapter(
                getActivity(), getActivity().getLayoutInflater(), leaderboardDefCache.getOrFetch(keys), R.layout.leaderboard_def_item);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>
}
