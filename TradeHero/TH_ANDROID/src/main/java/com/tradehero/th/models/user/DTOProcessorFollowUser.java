package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorFollowUser extends DTOProcessorUpdateUserProfile
{
    @NotNull protected final HeroListCache heroListCache;
    @NotNull protected final GetPositionsCache getPositionsCache;
    @NotNull protected final UserMessagingRelationshipCache userMessagingRelationshipCache;
    @NotNull protected final UserBaseKey userToFollow;

    public DTOProcessorFollowUser(
            @NotNull UserProfileCache userProfileCache,
            @NotNull HeroListCache heroListCache,
            @NotNull GetPositionsCache getPositionsCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @NotNull UserBaseKey userToFollow)
    {
        super(userProfileCache);
        this.heroListCache = heroListCache;
        this.getPositionsCache = getPositionsCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.userToFollow = userToFollow;
    }

    @Override public UserProfileDTO process(UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        heroListCache.invalidate(userToFollow);
        getPositionsCache.invalidate(userToFollow);
        userMessagingRelationshipCache.invalidate(userToFollow);
        return processed;
    }
}
