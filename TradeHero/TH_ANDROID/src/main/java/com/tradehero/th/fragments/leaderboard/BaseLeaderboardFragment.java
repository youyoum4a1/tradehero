package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: tho Date: 11/1/13 Time: 6:24 PM Copyright (c) TradeHero */
abstract public class BaseLeaderboardFragment extends BasePurchaseManagerFragment
        implements BaseFragment.TabBarVisibilityInformer
{
    public static final String BUNDLE_KEY_LEADERBOARD_ID = BaseLeaderboardFragment.class.getName() + ".leaderboardId";
    public static final String BUNDLE_KEY_LEADERBOARD_DEF_TITLE = BaseLeaderboardFragment.class.getName() + ".leaderboardDefTitle";
    public static final String BUNDLE_KEY_LEADERBOARD_DEF_DESC = BaseLeaderboardFragment.class.getName() + ".leaderboardDefDesc";

    @Inject LeaderboardSortHelper leaderboardSortHelper;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;

    protected UserProfileDTO currentUserProfileDTO;
    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileCacheFetchTask;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.userProfileCacheListener = new BaseLeaderboardFragmentProfileCacheListener();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
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
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();
        detachUserProfileCacheFetchTask();
        userProfileCacheFetchTask = userProfileCache.getOrFetch(currentUserId.toUserBaseKey(), userProfileCacheListener);
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
        bundle.putString(BUNDLE_KEY_LEADERBOARD_DEF_DESC, dto.desc);

        switch (dto.id)
        {
            case LeaderboardDefDTO.LEADERBOARD_FRIEND_ID:
                getNavigator().pushFragment(FriendLeaderboardMarkUserListFragment.class, bundle);
                break;
            case LeaderboardDefDTO.LEADERBOARD_HERO_ID :
                pushHeroFragment();
                break;
            case LeaderboardDefDTO.LEADERBOARD_FOLLOWER_ID :
                pushFollowerFragment();
                break;
            default:
                getNavigator().pushFragment(LeaderboardMarkUserListFragment.class, bundle);
                break;
        }
    }


    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(HeroManagerFragment.BUNDLE_KEY_FOLLOWER_ID, currentUserId.get());
        OwnedPortfolioId applicablePortfolio = userInteractor.getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(BasePurchaseManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolio.getArgs());
        }
        getNavigator().pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(FollowerManagerFragment.BUNDLE_KEY_HERO_ID, currentUserId.get());
        OwnedPortfolioId applicablePortfolio = userInteractor.getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(BasePurchaseManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolio.getArgs());
        }
        getNavigator().pushFragment(FollowerManagerFragment.class, bundle);
    }

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
            Timber.e("Failed to download current UserProfile", error);
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }
}
