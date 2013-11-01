package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 11/1/13 Time: 6:24 PM Copyright (c) TradeHero */
public class AbstractLeaderboardFragment extends DashboardFragment
        implements BaseFragment.TabBarVisibilityInformer
{
    public static final String TITLE = "LEADERBOARD_DEF_TITLE";
    public static final String CURRENT_SORT_TYPE = LeaderboardListViewFragment.class.getName() + ".currentSortType";

    @Inject protected LeaderboardSortHelper leaderboardSortHelper;

    private LeaderboardSortType currentSortType;
    private SortTypeChangedListener sortTypeChangeListener;
    private SubMenu sortSubMenu;
    private int flags;

    @Override public void onResume()
    {
        updateSortSubMenu(getArguments());
        super.onResume();
    }

    protected void pushLeaderboardListViewFragment(LeaderboardDefDTO dto)
    {
        Bundle bundle = new Bundle(getArguments());
        bundle.putInt(LeaderboardDTO.LEADERBOARD_ID, dto.getId());
        bundle.putString(LeaderboardListViewFragment.TITLE, dto.name);
        bundle.putInt(LeaderboardListViewFragment.CURRENT_SORT_TYPE, currentSortType != null ? currentSortType.getFlag() : dto.defaultSortTypeId);
        getNavigator().pushFragment(LeaderboardListViewFragment.class, bundle);
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

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        LeaderboardSortType selectedSortType = LeaderboardSortType.byFlag(item.getItemId());
        if (selectedSortType != null && selectedSortType != currentSortType)
        {
            setCurrentSortType(selectedSortType);
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    //<editor-fold desc="Sort menu stuffs">
    private void createSortSubMenu(Menu menu)
    {
        LeaderboardSortType sortType = getCurrentSortType();
        sortSubMenu = menu.addSubMenu("").setIcon(sortType.getSelectedResourceIcon());
        leaderboardSortHelper.addSortMenu(sortSubMenu, flags);
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

    protected LeaderboardSortType getCurrentSortType()
    {
        return currentSortType != null ? currentSortType : LeaderboardSortType.DefaultSortType;
    }

    public void setSortTypeChangeListener(SortTypeChangedListener sortTypeChangeListener)
    {
        this.sortTypeChangeListener = sortTypeChangeListener;
    }

    private void updateSortSubMenu(Bundle bundle)
    {
        flags = bundle.getInt(LeaderboardSortType.BUNDLE_FLAG);
        updateCurrentSortType(bundle);
    }

    protected void updateCurrentSortType(Bundle bundle)
    {
        setCurrentSortType(LeaderboardSortType.byFlag(bundle.getInt(CURRENT_SORT_TYPE)));
    }
    //</editor-fold>

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
