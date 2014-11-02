package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

abstract public class AbstractDTOProcessorFollowUser extends DTOProcessorUpdateUserProfile
{
    @NotNull protected final HeroListCacheRx heroListCache;
    @NotNull protected final GetPositionsCacheRx getPositionsCache;
    @NotNull protected final UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @NotNull protected final UserBaseKey followerId;
    @NotNull protected final UserBaseKey heroId;

    //<editor-fold desc="Constructors">
    public AbstractDTOProcessorFollowUser(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache,
            @NotNull HeroListCacheRx heroListCache,
            @NotNull GetPositionsCacheRx getPositionsCache,
            @NotNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NotNull UserBaseKey followerId,
            @NotNull UserBaseKey heroId)
    {
        super(userProfileCache, homeContentCache);
        this.heroListCache = heroListCache;
        this.getPositionsCache = getPositionsCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.followerId = followerId;
        this.heroId = heroId;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        heroListCache.invalidate(followerId);
        getPositionsCache.invalidate(heroId);
        return processed;
    }
}
