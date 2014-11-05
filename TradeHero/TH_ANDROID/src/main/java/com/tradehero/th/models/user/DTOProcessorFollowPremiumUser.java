package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;

public class DTOProcessorFollowPremiumUser extends AbstractDTOProcessorFollowUser
{
    @NonNull protected final AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorFollowPremiumUser(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache,
            @NonNull HeroListCacheRx heroListCache,
            @NonNull GetPositionsCacheRx getPositionsCache,
            @NonNull UserMessagingRelationshipCacheRx userMessagingRelationshipCache,
            @NonNull AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache,
            @NonNull UserBaseKey followerId,
            @NonNull UserBaseKey heroId)
    {
        super(userProfileCache,
                homeContentCache,
                heroListCache,
                getPositionsCache,
                userMessagingRelationshipCache,
                followerId,
                heroId);
        this.allowableRecipientPaginatedCache = allowableRecipientPaginatedCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        userMessagingRelationshipCache.markIsPremiumHero(heroId);
        allowableRecipientPaginatedCache.invalidateAll();
        return processed;
    }
}
