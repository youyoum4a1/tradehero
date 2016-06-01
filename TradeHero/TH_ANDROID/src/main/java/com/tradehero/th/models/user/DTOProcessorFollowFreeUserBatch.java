package com.ayondo.academy.models.user;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.social.BatchFollowFormDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.persistence.user.UserMessagingRelationshipCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

public class DTOProcessorFollowFreeUserBatch extends DTOProcessorUpdateUserProfile
{
    @NonNull protected final UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @NonNull protected final BatchFollowFormDTO followFormDTO;

    //<editor-fold desc="Constructors">
    public DTOProcessorFollowFreeUserBatch(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NonNull BatchFollowFormDTO followFormDTO)
    {
        super(userProfileCache);
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.followFormDTO = followFormDTO;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        UserBaseKey heroId;
        for (Integer userId : followFormDTO.userIds)
        {
            heroId = new UserBaseKey(userId);
            userMessagingRelationshipCache.markIsFreeHero(heroId);
        }
        return processed;
    }
}
