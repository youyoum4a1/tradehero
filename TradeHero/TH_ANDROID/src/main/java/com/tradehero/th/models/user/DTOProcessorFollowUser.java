package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;

public class DTOProcessorFollowUser extends DTOProcessorUpdateUserProfile
{
    protected final HeroListCache heroListCache;
    protected final GetPositionsCache getPositionsCache;
    protected final UserMessagingRelationshipCache userMessagingRelationshipCache;
    protected final UserBaseKey userToFollow;

    public DTOProcessorFollowUser(
            UserProfileCache userProfileCache,
            HeroListCache heroListCache,
            GetPositionsCache getPositionsCache,
            UserMessagingRelationshipCache userMessagingRelationshipCache,
            UserBaseKey userToFollow)
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
