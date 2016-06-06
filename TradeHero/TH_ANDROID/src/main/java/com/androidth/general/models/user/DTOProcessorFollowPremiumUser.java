package com.androidth.general.models.user;

import android.support.annotation.NonNull;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.persistence.social.HeroListCacheRx;
import com.androidth.general.persistence.user.UserMessagingRelationshipCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;

public class DTOProcessorFollowPremiumUser extends AbstractDTOProcessorFollowUser
{
    //<editor-fold desc="Constructors">
    public DTOProcessorFollowPremiumUser(
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
        userMessagingRelationshipCache.markIsPremiumHero(heroId);
        return processed;
    }
}
