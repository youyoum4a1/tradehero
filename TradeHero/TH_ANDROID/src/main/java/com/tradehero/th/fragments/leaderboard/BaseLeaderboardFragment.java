package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.UserOnLeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

abstract public class BaseLeaderboardFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_LEADERBOARD_ID = BaseLeaderboardFragment.class.getName() + ".leaderboardId";

    @Inject LeaderboardSortHelper leaderboardSortHelper;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject LeaderboardDefCache leaderboardDefCache;
    @Inject LeaderboardCache leaderboardCache;

    @NotNull protected LeaderboardDefKey leaderboardDefKey;
    @Nullable protected DTOCacheNew.Listener<LeaderboardDefKey, LeaderboardDefDTO> leaderboardDefCacheListener;
    protected LeaderboardDefDTO leaderboardDefDTO;
    @Nullable protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected UserProfileDTO currentUserProfileDTO;
    @Nullable protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;
    protected LeaderboardDTO leaderboardDTO;
    @Nullable protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> userOnLeaderboardCacheListener;
    protected LeaderboardUserDTO currentLeaderboardUserDTO;

    private View mRankHeaderView;

    public static void putLeaderboardDefKey(@NotNull Bundle args, @NotNull LeaderboardDefKey leaderboardDefKey)
    {
        args.putInt(BUNDLE_KEY_LEADERBOARD_ID, leaderboardDefKey.key);
    }

    @NotNull public static LeaderboardDefKey getLeadboardDefKey(@NotNull Bundle args)
    {
        return new LeaderboardDefKey(args.getInt(BUNDLE_KEY_LEADERBOARD_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardDefKey = getLeadboardDefKey(getArguments());
        this.leaderboardDefCacheListener = createLeaderboardDefCacheListener();
        this.userProfileCacheListener = createUserProfileListener();
        this.leaderboardCacheListener = createLeaderboardCacheListener();
        this.userOnLeaderboardCacheListener = createUserOnLeaderboardListener();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(getMenuResource(), menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();
        fetchLeaderboardDef();
        fetchCurrentUserProfile();
        fetchLeaderboard();
        fetchUserOnLeaderboard();
    }

    @Override public void onStop()
    {
        detachLeaderboardDefCacheListener();
        detachUserProfileCache();
        detachLeaderboardCacheListener();
        detachUserOnLeaderboardCacheListener();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        mRankHeaderView = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.leaderboardDefCacheListener = null;
        this.userProfileCacheListener = null;
        this.leaderboardCacheListener = null;
        this.userOnLeaderboardCacheListener = null;
        super.onDestroy();
    }

    protected void detachLeaderboardDefCacheListener()
    {
        leaderboardDefCache.unregister(leaderboardDefCacheListener);
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected void detachLeaderboardCacheListener()
    {
        leaderboardCache.unregister(leaderboardCacheListener);
    }

    protected void detachUserOnLeaderboardCacheListener()
    {
        leaderboardCache.unregister(userOnLeaderboardCacheListener);
    }

    protected void fetchLeaderboardDef()
    {
        detachLeaderboardDefCacheListener();
        leaderboardDefCache.register(leaderboardDefKey, leaderboardDefCacheListener);
        leaderboardDefCache.getOrFetchAsync(leaderboardDefKey);
    }

    protected void fetchCurrentUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected void fetchLeaderboard()
    {
        detachLeaderboardCacheListener();
        LeaderboardKey key = new LeaderboardKey(leaderboardDefKey.key);
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key);
    }

    protected void fetchUserOnLeaderboard()
    {
        detachUserOnLeaderboardCacheListener();
        UserOnLeaderboardKey userOnLeaderboardKey =
                new UserOnLeaderboardKey(new LeaderboardKey(leaderboardDefKey.key), currentUserId.toUserBaseKey());
        leaderboardCache.register(userOnLeaderboardKey, userOnLeaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(userOnLeaderboardKey);
    }

    protected int getMenuResource()
    {
        return R.menu.leaderboard_menu;
    }

    protected void pushLeaderboardListViewFragment(@NotNull LeaderboardDefDTO dto)
    {
        Bundle bundle = new Bundle(getArguments());

        switch (dto.id)
        {
            case LeaderboardDefKeyKnowledge.FRIEND_ID:
                pushFriendsFragment(dto);
                break;
            case LeaderboardDefKeyKnowledge.HERO_ID:
                pushHeroFragment();
                break;
            case LeaderboardDefKeyKnowledge.FOLLOWER_ID:
                pushFollowerFragment();
                break;
            default:
                Timber.d("LeaderboardMarkUserListFragment %s", bundle);
                LeaderboardMarkUserListFragment.putLeaderboardDefKey(bundle, dto.getLeaderboardDefKey());
                getDashboardNavigator().pushFragment(LeaderboardMarkUserListFragment.class, bundle);
                break;
        }
    }

    protected void pushFriendsFragment(LeaderboardDefDTO dto)
    {
        Bundle args = new Bundle();

        FriendLeaderboardMarkUserListFragment.putLeaderboardDefKey(args, dto.getLeaderboardDefKey());

        getDashboardNavigator().pushFragment(FriendLeaderboardMarkUserListFragment.class, args);
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(bundle, currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            HeroManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        getDashboardNavigator().pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowerManagerFragment.putHeroId(bundle, currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            //FollowerManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        getDashboardNavigator().pushFragment(FollowerManagerFragment.class, bundle);
    }

    /**
     * Get the header view which shows the user's current rank
     */
    @Nullable protected final View inflateAndGetUserRankHeaderView()
    {
        if (mRankHeaderView == null)
        {
            mRankHeaderView = LayoutInflater.from(getActivity()).inflate(getCurrentRankLayoutResId(), null, false);
            initCurrentRankHeaderView();
        }
        return mRankHeaderView;
    }

    protected void initCurrentRankHeaderView()
    {
        if (mRankHeaderView instanceof LeaderboardCurrentUserRankHeaderView)
        {
            LeaderboardCurrentUserRankHeaderView headerCopy = (LeaderboardCurrentUserRankHeaderView) mRankHeaderView;

            headerCopy.setApplicablePortfolioId(getApplicablePortfolioId());
            if (currentLeaderboardUserDTO != null)
            {
                headerCopy.display(currentLeaderboardUserDTO);
            }
        }
    }

    protected int getCurrentRankLayoutResId()
    {
        throw new RuntimeException("Not implemented!");
    }

    protected DTOCacheNew.Listener<LeaderboardDefKey, LeaderboardDefDTO> createLeaderboardDefCacheListener()
    {
        return new BaseLeaderboardFragmentLeaderboardDefCacheListener();
    }

    protected class BaseLeaderboardFragmentLeaderboardDefCacheListener implements DTOCacheNew.Listener<LeaderboardDefKey, LeaderboardDefDTO>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardDefKey key, @NotNull LeaderboardDefDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull LeaderboardDefKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_leaderboard_def);
        }
    }

    protected void linkWith(LeaderboardDefDTO leaderboardDefDTO, boolean andDisplay)
    {
        this.leaderboardDefDTO = leaderboardDefDTO;
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileListener()
    {
        return new BaseLeaderboardFragmentProfileCacheListener();
    }

    protected class BaseLeaderboardFragmentProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            setCurrentUserProfileDTO(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            Timber.e("Failed to download current UserProfile", error);
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    protected void setCurrentUserProfileDTO(@NotNull UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
    }

    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> createLeaderboardCacheListener()
    {
        return new BaseLeaderboardFragmentLeaderboardCacheListener();
    }

    protected class BaseLeaderboardFragmentLeaderboardCacheListener implements DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error)
        {
            Timber.e("Failed to leaderboard", error);
            THToast.show(R.string.error_fetch_leaderboard_info);
        }
    }

    protected void linkWith(@Nullable LeaderboardDTO leaderboardDTO, boolean andDisplay)
    {
        this.leaderboardDTO = leaderboardDTO;
        if (andDisplay)
        {
            if (leaderboardDTO != null)
            {
                setActionBarTitle(leaderboardDTO.name);
            }
        }
    }

    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> createUserOnLeaderboardListener()
    {
        return new BaseLeaderboardFragmentUserOnLeaderboardCacheListener();
    }

    protected class BaseLeaderboardFragmentUserOnLeaderboardCacheListener implements DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO value)
        {
            LeaderboardUserDTO received = null;
            if (value.users != null && value.users.size() == 1)
            {
                received = value.users.get(0);
            }
            linkWith(received, true);
        }

        @Override public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error)
        {
            Timber.e("Failed to download current User position on leaderboard", error);
            THToast.show(R.string.error_fetch_user_on_leaderboard);
            linkWith((LeaderboardUserDTO) null, true);
        }
    }

    protected void linkWith(@Nullable LeaderboardUserDTO leaderboardDTO, boolean andDisplay)
    {
        this.currentLeaderboardUserDTO = leaderboardDTO;
        if (andDisplay)
        {
            initCurrentRankHeaderView();
        }
    }
}
