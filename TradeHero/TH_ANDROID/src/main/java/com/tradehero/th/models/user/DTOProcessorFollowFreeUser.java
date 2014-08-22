package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorFollowFreeUser extends AbstractDTOProcessorFollowUser
{
    @NotNull protected final AllowableRecipientPaginatedCache allowableRecipientPaginatedCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorFollowFreeUser(
            @NotNull UserProfileCache userProfileCache,
            @NotNull HeroListCache heroListCache,
            @NotNull GetPositionsCache getPositionsCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @NotNull AllowableRecipientPaginatedCache allowableRecipientPaginatedCache,
            @NotNull UserBaseKey followerId,
            @NotNull UserBaseKey heroId)
    {
        super(userProfileCache,
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
        userMessagingRelationshipCache.markIsFreeHero(heroId);
        return processed;
    }
}
