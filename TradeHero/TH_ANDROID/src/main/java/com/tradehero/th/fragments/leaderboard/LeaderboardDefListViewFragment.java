package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
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
        DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final String TAG = LeaderboardDefListViewFragment.class.getName();
    public static final String TITLE = "LEADERBOARD_DEF_TITLE";

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected LeaderboardSortHelper leaderboardSortHelper;

    private int flags;
    private LeaderboardDefListAdapter leaderboardDefListAdapter;
    private LeaderboardSortType currentSortType;
    private SortTypeChangedListener sortTypeChangeListener;
    private SubMenu sortSubMenu;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public void onResume()
    {
        Bundle args = getArguments();

        updateLeaderboardDefListKey(args);
        updateSortSubMenu(args);

        leaderboardDefListAdapter = new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_def_item);
        setListAdapter(leaderboardDefListAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                LeaderboardDefDTO dto = (LeaderboardDefDTO) leaderboardDefListAdapter.getItem(position);
                if (dto != null)
                {
                    Bundle bundle = new Bundle(getArguments());
                    bundle.putInt(LeaderboardDTO.LEADERBOARD_ID, dto.getId());
                    bundle.putString(LeaderboardListViewFragment.TITLE, dto.name);

                    getNavigator().pushFragment(LeaderboardListViewFragment.class, bundle);
                }
            }
        });

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
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        Bundle args = getArguments();
        if (args != null)
        {
            String title = args.getString(TITLE);
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

    @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        List<LeaderboardDefDTO> leaderboardDefItems = null;
        try
        {
            leaderboardDefItems = leaderboardDefCache.get().getOrFetch(value);
        }
        catch (Throwable error)
        {
            onErrorThrown(key, error);
        }
        leaderboardDefListAdapter.setItems(leaderboardDefItems);
        leaderboardDefListAdapter.notifyDataSetChanged();
    }

    @Override public void onErrorThrown(LeaderboardDefListKey key, Throwable error)
    {
        THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
        THLog.e(TAG, "Error fetching the leaderboard def key list " + key, error);
    }

    public void setSortTypeChangeListener(SortTypeChangedListener sortTypeChangeListener)
    {
        this.sortTypeChangeListener = sortTypeChangeListener;
    }
}
