package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.BasePagedListRxFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import java.util.List;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import timber.log.Timber;

abstract public class BaseLeaderboardPagedListRxFragment<
        PagedDTOKeyType extends PagedDTOKey,
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>,
        ContainerDTOType extends DTO & ContainerDTO<DTOType, DTOListType>>
        extends BasePagedListRxFragment<
        PagedDTOKeyType,
        DTOType,
        DTOListType,
        ContainerDTOType>
{
    private static final String BUNDLE_KEY_LEADERBOARD_ID = BaseLeaderboardPagedListRxFragment.class.getName() + ".leaderboardId";

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject LeaderboardDefCacheRx leaderboardDefCache;

    @NonNull protected LeaderboardDefKey leaderboardDefKey;
    protected LeaderboardDefDTO leaderboardDefDTO;
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
        leaderboardDefKey = getLeadboardDefKey(getArguments());
        super.onCreate(savedInstanceState);
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

    protected void fetchLeaderboardDef()
    {
        if (leaderboardDefKey.key > 0)
        {
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    leaderboardDefCache.get(leaderboardDefKey)
                            .map(new PairGetSecond<>()))
                    .subscribe(
                            this::linkWith,
                            e -> THToast.show(R.string.error_fetch_leaderboard_def)));
        }
        else
        {
            Timber.d("Skipping fetching leaderboardDef for key %d", leaderboardDefKey.key);
        }
    }

    protected void linkWith(LeaderboardDefDTO leaderboardDefDTO)
    {
        this.leaderboardDefDTO = leaderboardDefDTO;
        if (leaderboardDefDTO != null
                && leaderboardDefDTO.name != null
                && !leaderboardDefDTO.name.isEmpty())
        {
            setActionBarTitle(leaderboardDefDTO.name);
        }
    }

    protected void fetchCurrentUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<>()))
                .subscribe(
                        this::setCurrentUserProfileDTO,
                        e -> {
                            Timber.e(e, "Failed to download current UserProfile");
                            THToast.show(R.string.error_fetch_your_user_profile);
                        }));
    }

    protected void setCurrentUserProfileDTO(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
    }

    @MenuRes protected int getMenuResource()
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
}
