package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;

abstract public class AbstractDTOProcessorFollowUser extends DTOProcessorUpdateUserProfile
{
    @NonNull protected final HeroListCacheRx heroListCache;
    @NonNull protected final GetPositionsCacheRx getPositionsCache;
    @NonNull protected final UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @NonNull protected final UserBaseKey followerId;
    @NonNull protected final UserBaseKey heroId;

    //<editor-fold desc="Constructors">
    public AbstractDTOProcessorFollowUser(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache,
            @NonNull HeroListCacheRx heroListCache,
            @NonNull GetPositionsCacheRx getPositionsCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NonNull UserBaseKey followerId,
            @NonNull UserBaseKey heroId)
    {
        super(userProfileCache, homeContentCache);
        this.heroListCache = heroListCache;
        this.getPositionsCache = getPositionsCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.followerId = followerId;
        this.heroId = heroId;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        heroListCache.invalidate(followerId);
        getPositionsCache.invalidate(heroId);
        return processed;
    }
}
