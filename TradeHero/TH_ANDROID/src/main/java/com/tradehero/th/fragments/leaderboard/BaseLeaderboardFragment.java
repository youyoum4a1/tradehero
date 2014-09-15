package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.user.UserProfileCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import timber.log.Timber;

abstract public class BaseLeaderboardFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_LEADERBOARD_ID = BaseLeaderboardFragment.class.getName() + ".leaderboardId";

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject LeaderboardDefCache leaderboardDefCache;
    @Inject LeaderboardCache leaderboardCache;

    @NotNull protected LeaderboardDefKey leaderboardDefKey;
    @Nullable protected DTOCacheNew.Listener<LeaderboardDefKey, LeaderboardDefDTO> leaderboardDefCacheListener;
    protected LeaderboardDefDTO leaderboardDefDTO;
    @Nullable protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected UserProfileDTO currentUserProfileDTO;
    @Inject DashboardNavigator navigator;

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
    }

    @Override public void onStop()
    {
        detachLeaderboardDefCacheListener();
        detachUserProfileCache();
        super.onStop();
    }


    @Override public void onDestroy()
    {
        this.leaderboardDefCacheListener = null;
        this.userProfileCacheListener = null;
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
            case LeaderboardDefKeyKnowledge.INVITE_FRIENDS_ID:
                pushInvitationFragment();
                break;
            default:
                Timber.d("LeaderboardMarkUserListFragment %s", bundle);
                LeaderboardMarkUserListFragment.putLeaderboardDefKey(bundle, dto.getLeaderboardDefKey());
                navigator.pushFragment(LeaderboardMarkUserListFragment.class, bundle);
                break;
        }
    }

    protected void pushFriendsFragment(LeaderboardDefDTO dto)
    {
        Bundle args = new Bundle();
        FriendLeaderboardMarkUserListFragment.putLeaderboardDefKey(args, dto.getLeaderboardDefKey());
        navigator.pushFragment(FriendLeaderboardMarkUserListFragment.class, args);
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
        navigator.pushFragment(HeroManagerFragment.class, bundle);
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
        navigator.pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushInvitationFragment()
    {
        if (navigator != null)
        {
            navigator.pushFragment(FriendsInvitationFragment.class);
        }
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
        if (andDisplay)
        {
            if (leaderboardDefDTO != null
                    && leaderboardDefDTO.name != null
                    && !leaderboardDefDTO.name.isEmpty())
            {
                setActionBarTitle(leaderboardDefDTO.name);
            }
        }
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
}
