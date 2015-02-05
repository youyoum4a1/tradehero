package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardCacheRx;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import timber.log.Timber;

abstract public class BaseLeaderboardFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_LEADERBOARD_ID = BaseLeaderboardFragment.class.getName() + ".leaderboardId";

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject LeaderboardDefCacheRx leaderboardDefCache;
    @Inject LeaderboardCacheRx leaderboardCache;

    @NonNull protected LeaderboardDefKey leaderboardDefKey;
    @Nullable protected Subscription leaderboardDefCacheSubscription;
    protected LeaderboardDefDTO leaderboardDefDTO;
    @Nullable protected Subscription currentUserProfileSubscription;
    protected UserProfileDTO currentUserProfileDTO;

    public static void putLeaderboardDefKey(@NonNull Bundle args, @NonNull LeaderboardDefKey leaderboardDefKey)
    {
        args.putInt(BUNDLE_KEY_LEADERBOARD_ID, leaderboardDefKey.key);
    }

    @NonNull public static LeaderboardDefKey getLeadboardDefKey(@NonNull Bundle args)
    {
        return new LeaderboardDefKey(args.getInt(BUNDLE_KEY_LEADERBOARD_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardDefKey = getLeadboardDefKey(getArguments());
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(getMenuResource(), menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onStart()
    {
        super.onStart();
        fetchLeaderboardDef();
        fetchCurrentUserProfile();
    }

    @Override public void onStop()
    {
        unsubscribe(leaderboardDefCacheSubscription);
        leaderboardDefCacheSubscription = null;
        unsubscribe(currentUserProfileSubscription);
        currentUserProfileSubscription = null;
        super.onStop();
    }

    protected void fetchLeaderboardDef()
    {
        unsubscribe(leaderboardDefCacheSubscription);
        if (leaderboardDefKey.key > 0)
        {
            leaderboardDefCacheSubscription = AppObservable.bindFragment(
                    this,
                    leaderboardDefCache.get(leaderboardDefKey))
                    .subscribe(createLeaderboardDefCacheObserver()
                    );
        }
        else
        {
            Timber.d("Skipping fetching leaderboardDef for key %d", leaderboardDefKey.key);
        }
    }

    protected void fetchCurrentUserProfile()
    {
        unsubscribe(currentUserProfileSubscription);
        currentUserProfileSubscription = AppObservable.bindFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .subscribe(createUserProfileObserver());
    }

    protected int getMenuResource()
    {
        return R.menu.empty_menu;
    }

    protected void pushLeaderboardListViewFragment(@NonNull LeaderboardDefDTO dto)
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
                navigator.get().pushFragment(LeaderboardMarkUserListFragment.class, bundle);
                break;
        }
    }

    protected void pushFriendsFragment(LeaderboardDefDTO dto)
    {
        Bundle args = new Bundle();
        FriendLeaderboardMarkUserListFragment.putLeaderboardDefKey(args, dto.getLeaderboardDefKey());
        navigator.get().pushFragment(FriendLeaderboardMarkUserListFragment.class, args);
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
        navigator.get().pushFragment(HeroManagerFragment.class, bundle);
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
        navigator.get().pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushInvitationFragment()
    {
        if (navigator != null)
        {
            navigator.get().pushFragment(FriendsInvitationFragment.class);
        }
    }

    protected Observer<Pair<LeaderboardDefKey, LeaderboardDefDTO>> createLeaderboardDefCacheObserver()
    {
        return new BaseLeaderboardFragmentLeaderboardDefCacheObserver();
    }

    protected class BaseLeaderboardFragmentLeaderboardDefCacheObserver implements Observer<Pair<LeaderboardDefKey, LeaderboardDefDTO>>
    {
        @Override public void onNext(Pair<LeaderboardDefKey, LeaderboardDefDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
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

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileObserver()
    {
        return new BaseLeaderboardFragmentProfileCacheObserver();
    }

    protected class BaseLeaderboardFragmentProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            setCurrentUserProfileDTO(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e("Failed to download current UserProfile", e);
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    protected void setCurrentUserProfileDTO(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
    }
}
