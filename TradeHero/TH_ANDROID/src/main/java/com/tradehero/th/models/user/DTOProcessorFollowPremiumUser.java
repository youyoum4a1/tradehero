package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorFollowPremiumUser extends AbstractDTOProcessorFollowUser
{
    @NotNull protected final AllowableRecipientPaginatedCache allowableRecipientPaginatedCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorFollowPremiumUser(
            @NotNull UserProfileCache userProfileCache,
            @NotNull HeroListCache heroListCache,
            @NotNull GetPositionsCache getPositionsCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @NotNull AllowableRecipientPaginatedCache allowableRecipientPaginatedCache,
            @NotNull UserBaseKey userToFollow)
    {
        super(userProfileCache, heroListCache, getPositionsCache, userMessagingRelationshipCache, userToFollow);
        this.allowableRecipientPaginatedCache = allowableRecipientPaginatedCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        userMessagingRelationshipCache.markIsPremiumHero(userToFollow);
        return processed;
    }
}
