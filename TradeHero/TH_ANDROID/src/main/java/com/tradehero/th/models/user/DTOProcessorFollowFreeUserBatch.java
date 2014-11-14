package com.tradehero.th.models.user;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.friend.BatchFollowFormDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorFollowFreeUserBatch extends DTOProcessorUpdateUserProfile
{
    @NonNull protected final UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @NonNull protected final BatchFollowFormDTO followFormDTO;

    //<editor-fold desc="Constructors">
    public DTOProcessorFollowFreeUserBatch(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NonNull BatchFollowFormDTO followFormDTO)
    {
        super(userProfileCache, homeContentCache);
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
