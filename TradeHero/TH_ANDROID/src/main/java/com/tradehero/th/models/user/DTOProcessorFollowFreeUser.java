package com.tradehero.th.models.user;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorFollowFreeUser extends AbstractDTOProcessorFollowUser
{
    //<editor-fold desc="Constructors">
    public DTOProcessorFollowFreeUser(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HeroListCacheRx heroListCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NonNull UserBaseKey followerId,
            @NonNull UserBaseKey heroId)
    {
        super(userProfileCache,
                heroListCache,
                userMessagingRelationshipCache,
                followerId,
                heroId);
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        userMessagingRelationshipCache.markIsFreeHero(heroId);
        return processed;
    }
}
