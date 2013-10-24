package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardListFragment;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 7:21 PM Copyright (c) TradeHero */
public class LeaderboardDefListViewFragment extends DashboardListFragment
    implements
        BaseFragment.ArgumentsChangeListener,
        DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final String TAG = LeaderboardDefListViewFragment.class.getName();
    public static final String TITLE = "LEADERBOARD_DEF_TITLE";

    private LeaderboardDefListAdapter leaderboardDefListAdapter;

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected LeaderboardSortHelper leaderboardSortHelper;

    private Bundle desiredArguments;
    private int flags;
    private LeaderboardSortType currentSortType;
    private SortTypeChangedListener sortTypeChangeListener;
    private SubMenu sortSubMenu;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public void onResume()
    {
        if (desiredArguments == null)
        {
            desiredArguments = getArguments();
        }

        updateLeaderboardDefListKey(desiredArguments);
        updateSortSubMenu(desiredArguments);

        leaderboardDefListAdapter = new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_def_item);
        setListAdapter(leaderboardDefListAdapter);

        super.onResume();
    }

    private void updateSortSubMenu(Bundle bundle)
    {
        flags = bundle.getInt(LeaderboardSortType.BUNDLE_FLAG);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        createSortSubMenu(menu);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (desiredArguments != null)
        {
            String title = desiredArguments.getString(TITLE);
            actionBar.setTitle(title == null ? "" : title);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void createSortSubMenu(Menu menu)
    {
        LeaderboardSortType sortType = getCurrentSortType();
        sortSubMenu = menu.addSubMenu("").setIcon(sortType.getSelectedResourceIcon());
        leaderboardSortHelper.addSortMenu(sortSubMenu, flags);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        LeaderboardSortType selectedSortType = LeaderboardSortType.byFlag(item.getItemId());
        if (selectedSortType != null && selectedSortType != currentSortType)
        {
            setCurrentSortType(selectedSortType);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCurrentSortType(LeaderboardSortType selectedSortType)
    {
        currentSortType = selectedSortType;
        sortSubMenu.setIcon(currentSortType.getSelectedResourceIcon());
        if (sortTypeChangeListener != null)
        {
            sortTypeChangeListener.onSortTypeChange(currentSortType);
        }
    }

    private LeaderboardSortType getCurrentSortType()
    {
        return currentSortType != null ? currentSortType : LeaderboardSortType.DefaultSortType;
    }
    //</editor-fold>

    private void updateLeaderboardDefListKey(Bundle bundle)
    {
        LeaderboardDefListKey key = new LeaderboardDefListKey(bundle);
        leaderboardDefListCache.get().getOrFetch(key, false, this).execute();
    }

    @Override public void onArgumentsChanged(Bundle args)
    {
        desiredArguments = args;
    }

    @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        List<LeaderboardDefDTO> leaderboardDefItems = leaderboardDefCache.get().getOrFetch(value);
        leaderboardDefListAdapter.setItems(leaderboardDefItems);
        leaderboardDefListAdapter.notifyDataSetChanged();
    }

    public void setSortTypeChangeListener(SortTypeChangedListener sortTypeChangeListener)
    {
        this.sortTypeChangeListener = sortTypeChangeListener;
    }
}
