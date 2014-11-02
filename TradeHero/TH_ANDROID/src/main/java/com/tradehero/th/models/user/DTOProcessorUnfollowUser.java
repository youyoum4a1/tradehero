package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUnfollowUser extends AbstractDTOProcessorFollowUser
{
    @NotNull protected final AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorUnfollowUser(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache,
            @NotNull HeroListCacheRx heroListCache,
            @NotNull GetPositionsCacheRx getPositionsCache,
            @NotNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NotNull AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache,
            @NotNull UserBaseKey followerId,
            @NotNull UserBaseKey heroId)
    {
        super(userProfileCache,
                homeContentCache,
                heroListCache,
                getPositionsCache,
                userMessagingRelationshipCache,
                followerId,
                heroId);
        this.allowableRecipientPaginatedCache = allowableRecipientPaginatedCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        userMessagingRelationshipCache.markNotHero(heroId);
        allowableRecipientPaginatedCache.invalidateAll();
        return processed;
    }
}
