package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefExchangeListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefMostSkilledListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefSectorListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefTimePeriodListKey;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class LeaderboardCommunityFragment extends BaseLeaderboardFragment
        implements DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final String TAG = LeaderboardCommunityFragment.class.getName();

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected Lazy<ProviderListCache> providerCache;

    private boolean fetched = false;
    private ListView mostSkilledListView;
    private ListView timePeriodListView;
    private ListView sectorListView;
    private LeaderboardDefListAdapter mostSkilledListAdapter;
    private LeaderboardDefListAdapter timePeriodListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_community_screen, container, false);
        initViews(view);
        prepareAdapters();
        return view;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    private void prepareAdapters()
    {
        leaderboardDefListCache.get().getOrFetch(new LeaderboardDefMostSkilledListKey(), false, this).execute();
    }

    private void initViews(View view)
    {
        mostSkilledListView = (ListView) view.findViewById(R.id.leaderboard_most_skilled);
        timePeriodListView = (ListView) view.findViewById(R.id.leaderboard_time_period);
        sectorListView = (ListView) view.findViewById(R.id.leaderboard_sector);

        ListView[] listViews = new ListView[] { mostSkilledListView, timePeriodListView, sectorListView };
        for (ListView listView: listViews)
        {
            listView.setEmptyView(view.findViewById(android.R.id.empty));
        }
    }

    @Override public void onStop()
    {
        super.onStop();
        fetched = false;
    }

    private AdapterView.OnItemClickListener createLeaderboardItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                LeaderboardDefDTO dto = (LeaderboardDefDTO) adapterView.getAdapter().getItem(position);

                if (dto != null)
                {
                    pushLeaderboardListViewFragment(dto);
                }
            }
        };
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        int defaultSortFlags = LeaderboardSortType.HeroQuotient.getFlag() | LeaderboardSortType.Roi.getFlag();
        getArguments().putInt(LeaderboardSortType.BUNDLE_FLAG, defaultSortFlags);

        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(getString(R.string.leaderboards));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO switch sorting type for leaderboard
        switch (item.getItemId())
        {
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    //<editor-fold desc="DTOCache Listeners">
    @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        // hide loading
        if (!fetched)
        {
            fetched = true;
            leaderboardDefListCache.get().getOrFetch(new LeaderboardDefTimePeriodListKey(), false, this).execute();
            leaderboardDefListCache.get().getOrFetch(new LeaderboardDefSectorListKey(), false, this).execute();
            leaderboardDefListCache.get().getOrFetch(new LeaderboardDefExchangeListKey(), false, this).execute();
        }

        if (value != null)
        {
            if (key instanceof LeaderboardDefMostSkilledListKey)
            {
                initMostSkilledListView(key, value);
            }
            else if (key instanceof LeaderboardDefTimePeriodListKey)
            {
                initTimePeriodListView(key, value);
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

    @Override public void onErrorThrown(LeaderboardDefListKey key, Throwable error)
    {
        THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
        THLog.e(TAG, "Error fetching the leaderboard def key list " + key, error);
    }
    //</editor-fold>

    //<editor-fold desc="Init some default LeaderboardDefDTOs - Hardcoded">
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

    //<editor-fold desc="ListView adapters creation">
    private void addItemToSectorSection(LeaderboardDefDTO dto)
    {
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
                    switch (dto.id)
                    {
                        case LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID:
                        {
                            Bundle bundle = new Bundle(getArguments());
                            (new LeaderboardDefSectorListKey()).putParameters(bundle);
                            bundle.putString(LeaderboardDefListViewFragment.TITLE, getString(R.string.leaderboard_sector));
                            bundle.putInt(BaseLeaderboardFragment.CURRENT_SORT_TYPE, getCurrentSortType().getFlag());
                            getNavigator().pushFragment(LeaderboardDefListViewFragment.class, bundle);
                        } break;
                        case LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID:
                        {
                            Bundle bundle = new LeaderboardDefExchangeListKey().getArgs();
                            bundle.putString(LeaderboardDefListViewFragment.TITLE, getString(R.string.leaderboard_exchange));
                            bundle.putInt(BaseLeaderboardFragment.CURRENT_SORT_TYPE, getCurrentSortType().getFlag());
                            getNavigator().pushFragment(LeaderboardDefListViewFragment.class, bundle);
                        } break;
                    }
                }
            }
        });
    }

    private LeaderboardDefListAdapter createTimePeriodListAdapter(List<LeaderboardDefDTO> timePeriodItems)
    {
        // sort time period items by number of days
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
        return new LeaderboardDefListAdapter(
                getActivity(), getActivity().getLayoutInflater(), timePeriodItems, R.layout.leaderboard_def_item);
    }

    private LeaderboardDefMostSkilledListAdapter createMostSkilledListAdapter(List<LeaderboardDefDTO> values)
    {
        return new LeaderboardDefMostSkilledListAdapter(
                getActivity(), getActivity().getLayoutInflater(), values, R.layout.leaderboard_def_item);
    }
    //</editor-fold>

    //<editor-fold desc="ListViews creation">
    private void initMostSkilledListView(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        try
        {
            mostSkilledListAdapter = createMostSkilledListAdapter(leaderboardDefCache.get().getOrFetch(value));
            mostSkilledListView.setAdapter(mostSkilledListAdapter);
        }
        catch (Throwable throwable)
        {
            onErrorThrown(key, throwable);
        }
        mostSkilledListView.setOnItemClickListener(createLeaderboardItemClickListener());
    }

    private void initTimePeriodListView(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        try
        {
            timePeriodListAdapter = createTimePeriodListAdapter(leaderboardDefCache.get().getOrFetch(value));
            timePeriodListView.setAdapter(timePeriodListAdapter);
        }
        catch (Throwable throwable)
        {
            onErrorThrown(key, throwable);
        }
        timePeriodListView.setOnItemClickListener(createLeaderboardItemClickListener());
    }
    //</editor-fold>

    //<editor-fold desc="Sorting">
    @Override protected void initSortTypeFromArguments()
    {
        // I'm the leaderboard master screen, one can change my sort type :))
    }

    @Override protected void onCurrentSortTypeChanged()
    {
        if (timePeriodListAdapter != null)
        {
            timePeriodListAdapter.setSortType(getCurrentSortType());
            timePeriodListAdapter.notifyDataSetChanged();
        }

        if (mostSkilledListAdapter != null)
        {
            mostSkilledListAdapter.setSortType(getCurrentSortType());
            mostSkilledListAdapter.notifyDataSetChanged();
        }
    }
    //</editor-fold>

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>
}
