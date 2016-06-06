package com.androidth.general.models.user;

import android.support.annotation.NonNull;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.persistence.social.HeroListCacheRx;
import com.androidth.general.persistence.user.UserMessagingRelationshipCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;

abstract public class AbstractDTOProcessorFollowUser extends DTOProcessorUpdateUserProfile
{
    @NonNull protected final HeroListCacheRx heroListCache;
    @NonNull protected final UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @NonNull protected final UserBaseKey followerId;
    @NonNull protected final UserBaseKey heroId;

    //<editor-fold desc="Constructors">
    public AbstractDTOProcessorFollowUser(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HeroListCacheRx heroListCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NonNull UserBaseKey followerId,
            @NonNull UserBaseKey heroId)
    {
        super(userProfileCache);
        this.heroListCache = heroListCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.followerId = followerId;
        this.heroId = heroId;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        heroListCache.invalidate(followerId);
        return processed;
    }
}
