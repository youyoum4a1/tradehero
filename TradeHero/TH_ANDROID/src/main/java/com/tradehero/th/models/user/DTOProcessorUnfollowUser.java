package com.tradehero.th.models.user;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorUnfollowUser extends AbstractDTOProcessorFollowUser
{
    //<editor-fold desc="Constructors">
    public DTOProcessorUnfollowUser(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache,
            @NonNull HeroListCacheRx heroListCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NonNull UserBaseKey followerId,
            @NonNull UserBaseKey heroId)
    {
        super(userProfileCache,
                homeContentCache,
                heroListCache,
                userMessagingRelationshipCache,
                followerId,
                heroId);
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        userMessagingRelationshipCache.markNotHero(heroId);
        return processed;
    }
}
