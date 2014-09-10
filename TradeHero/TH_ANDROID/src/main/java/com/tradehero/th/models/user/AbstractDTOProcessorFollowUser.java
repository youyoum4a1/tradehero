package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

abstract public class AbstractDTOProcessorFollowUser extends DTOProcessorUpdateUserProfile
{
    @NotNull protected final HeroListCache heroListCache;
    @NotNull protected final GetPositionsCache getPositionsCache;
    @NotNull protected final UserMessagingRelationshipCache userMessagingRelationshipCache;
    @NotNull protected final UserBaseKey followerId;
    @NotNull protected final UserBaseKey heroId;

    //<editor-fold desc="Constructors">
    public AbstractDTOProcessorFollowUser(
            @NotNull UserProfileCache userProfileCache,
            @NotNull HeroListCache heroListCache,
            @NotNull GetPositionsCache getPositionsCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @NotNull UserBaseKey followerId,
            @NotNull UserBaseKey heroId)
    {
        super(userProfileCache);
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
