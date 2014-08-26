package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderCompactCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserServiceWrapperStub extends UserServiceWrapper
{
    @NotNull Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserServiceWrapperStub(
            @NotNull UserService userService,
            @NotNull UserServiceAsync userServiceAsync,
            @NotNull CurrentUserId currentUserId,
            @NotNull DTOCacheUtil dtoCacheUtil,
            @NotNull UserProfileCache userProfileCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @NotNull Lazy<HeroListCache> heroListCache,
            @NotNull GetPositionsCache getPositionsCache,
            @NotNull Lazy<LeaderboardFriendsCache> leaderboardFriendsCache,
            @NotNull Lazy<ProviderListCache> providerListCache,
            @NotNull Lazy<ProviderCache> providerCache,
            @NotNull Lazy<ProviderCompactCache> providerCompactCache,
            @NotNull Lazy<AllowableRecipientPaginatedCache> allowableRecipientPaginatedCache,
            @NotNull Lazy<LeaderboardServiceWrapper> leaderboardServiceWrapper)
    {
        super(userService, userServiceAsync, currentUserId, dtoCacheUtil,
                userProfileCache, userMessagingRelationshipCache, heroListCache,
                getPositionsCache, leaderboardFriendsCache, providerListCache,
                providerCache, providerCompactCache, allowableRecipientPaginatedCache);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override public LeaderboardUserDTOList suggestHeroes(@NotNull SuggestHeroesListType suggestHeroesListType)
    {
        return leaderboardServiceWrapper.get().getLeaderboard(
                new LeaderboardKey(LeaderboardDefKeyKnowledge.MOST_SKILLED_ID))
                .users;
    }
}
