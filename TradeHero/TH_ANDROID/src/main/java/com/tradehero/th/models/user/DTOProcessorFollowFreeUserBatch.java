package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.friend.BatchFollowFormDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorFollowFreeUserBatch extends DTOProcessorUpdateUserProfile
{
    @NotNull protected final HeroListCacheRx heroListCache;
    @NotNull protected final GetPositionsCacheRx getPositionsCache;
    @NotNull protected final UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @NotNull protected final AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache;
    @NotNull protected final BatchFollowFormDTO followFormDTO;

    //<editor-fold desc="Constructors">
    public DTOProcessorFollowFreeUserBatch(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache,
            @NotNull HeroListCacheRx heroListCache,
            @NotNull GetPositionsCacheRx getPositionsCache,
            @NotNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NotNull AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache,
            @NotNull BatchFollowFormDTO followFormDTO)
    {
        super(userProfileCache, homeContentCache);
        this.heroListCache = heroListCache;
        this.getPositionsCache = getPositionsCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.allowableRecipientPaginatedCache = allowableRecipientPaginatedCache;
        this.followFormDTO = followFormDTO;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        UserBaseKey heroId;
        for (Integer userId : followFormDTO.userIds)
        {
            heroId = new UserBaseKey(userId);
            heroListCache.invalidate(heroId);
            getPositionsCache.invalidate(heroId);
            userMessagingRelationshipCache.markIsFreeHero(heroId);
        }
        allowableRecipientPaginatedCache.invalidateAll();
        return processed;
    }
}
