package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 11/1/13 Time: 6:24 PM Copyright (c) TradeHero */
abstract public class BaseLeaderboardFragment extends BasePurchaseManagerFragment
        implements BaseFragment.TabBarVisibilityInformer
{
    public static final String BUNDLE_KEY_LEADERBOARD_ID = BaseLeaderboardFragment.class.getName() + ".leaderboardId";
    public static final String BUNDLE_KEY_LEADERBOARD_DEF_TITLE = BaseLeaderboardFragment.class.getName() + ".leaderboardDefTitle";
    public static final String BUNDLE_KEY_LEADERBOARD_DEF_DESC = BaseLeaderboardFragment.class.getName() + ".leaderboardDefDesc";
    public static final String BUNDLE_KEY_CURRENT_SORT_TYPE = BaseLeaderboardFragment.class.getName() + ".currentSortType";
    public static final String BUNDLE_KEY_SORT_OPTION_FLAGS = BaseLeaderboardFragment.class.getName() + ".sortOptionFlags";

    @Inject protected LeaderboardSortHelper leaderboardSortHelper;
    @Inject protected CurrentUserId currentUserId;
    protected UserProfileDTO currentUserProfileDTO;
    @Inject protected UserProfileCache userProfileCache;
    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileCacheFetchTask;

    private LeaderboardSortType currentSortType;
    private SortTypeChangedListener sortTypeChangeListener;
    private SubMenu sortSubMenu;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.userProfileCacheListener = new BaseLeaderboardFragmentProfileCacheListener();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        createSortSubMenu(menu);
        initSortTypeFromArguments();

        inflater.inflate(getMenuResource(), menu);
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        Bundle args = getArguments();
        if (args != null)
        {
            String title = args.getString(BUNDLE_KEY_LEADERBOARD_DEF_TITLE);
            actionBar.setTitle(title == null ? "" : title);
        }
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

    @Override public void onResume()
    {
        super.onResume();
        detachUserProfileCacheFetchTask();
        userProfileCacheFetchTask = userProfileCache.getOrFetch(currentUserId.toUserBaseKey(), false, userProfileCacheListener);
        userProfileCacheFetchTask.execute();
    }

    @Override public void onDestroyView()
    {
        detachUserProfileCacheFetchTask();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.userProfileCacheListener = null;
        super.onDestroy();
    }

    protected void detachUserProfileCacheFetchTask()
    {
        if (userProfileCacheFetchTask != null)
        {
            userProfileCacheFetchTask.setListener(null);
        }
        userProfileCacheFetchTask = null;
    }

    protected int getMenuResource()
    {
        return R.menu.leaderboard_menu;
    }

    protected void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
    }

    protected void pushLeaderboardListViewFragment(LeaderboardDefDTO dto)
    {
        Bundle bundle = new Bundle(getArguments());
        bundle.putInt(BUNDLE_KEY_LEADERBOARD_ID, dto.id);
        bundle.putString(BUNDLE_KEY_LEADERBOARD_DEF_TITLE, dto.name);
        bundle.putInt(BUNDLE_KEY_CURRENT_SORT_TYPE, getCurrentSortType() != null ? getCurrentSortType().getFlag() : dto.getDefaultSortType().getFlag());
        bundle.putString(BUNDLE_KEY_LEADERBOARD_DEF_DESC, dto.desc);
        bundle.putInt(BUNDLE_KEY_SORT_OPTION_FLAGS, dto.getSortOptionFlags());

        switch (dto.id)
        {
            case LeaderboardDefDTO.LEADERBOARD_FRIEND_ID:
                getNavigator().pushFragment(FriendLeaderboardMarkUserListViewFragment.class, bundle);
                break;

            default:
                getNavigator().pushFragment(LeaderboardMarkUserListViewFragment.class, bundle);
                break;
        }
    }

    //<editor-fold desc="Sort menu stuffs">
    private void createSortSubMenu(Menu menu)
    {
        LeaderboardSortType sortType = getCurrentSortType();
        sortSubMenu = menu.addSubMenu("").setIcon(sortType.getSelectedResourceIcon());
        //leaderboardSortHelper.addSortMenu(sortSubMenu, getSortMenuFlag());
    }

    private int getSortMenuFlag()
    {
        return getArguments().getInt(BUNDLE_KEY_SORT_OPTION_FLAGS);
    }

    protected void setCurrentSortType(LeaderboardSortType selectedSortType)
    {
        if (sortTypeChangeListener != null && currentSortType != selectedSortType)
        {
            sortTypeChangeListener.onSortTypeChange(selectedSortType);
        }
        sortSubMenu.setIcon(selectedSortType.getSelectedResourceIcon());
        currentSortType = selectedSortType;
        onCurrentSortTypeChanged();
    }

    protected LeaderboardSortType getCurrentSortType()
    {
        return currentSortType != null ? currentSortType : LeaderboardSortType.DefaultSortType;
    }

    public void setSortTypeChangeListener(SortTypeChangedListener sortTypeChangeListener)
    {
        this.sortTypeChangeListener = sortTypeChangeListener;
    }

    protected void initSortTypeFromArguments()
    {
        setCurrentSortType(LeaderboardSortType.byFlag(getArguments().getInt(BUNDLE_KEY_CURRENT_SORT_TYPE)));
    }

    protected void onCurrentSortTypeChanged()
    {
        // do nothing
    }

    //</editor-fold>

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected class BaseLeaderboardFragmentProfileCacheListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        public BaseLeaderboardFragmentProfileCacheListener()
        {
            super();
        }

        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            setCurrentUserProfileDTO(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THLog.e(TAG, "Failed to download current UserProfile", error);
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }
}
