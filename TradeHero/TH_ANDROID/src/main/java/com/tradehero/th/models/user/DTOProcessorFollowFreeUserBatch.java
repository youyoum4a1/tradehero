package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.friend.BatchFollowFormDTO;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;

import org.jetbrains.annotations.NotNull;

public class DTOProcessorFollowFreeUserBatch extends DTOProcessorUpdateUserProfile
{
    @NotNull protected final HeroListCache heroListCache;
    @NotNull protected final GetPositionsCache getPositionsCache;
    @NotNull protected final UserMessagingRelationshipCache userMessagingRelationshipCache;
    @NotNull protected final AllowableRecipientPaginatedCache allowableRecipientPaginatedCache;
    @NotNull protected final BatchFollowFormDTO followFormDTO;

    //<editor-fold desc="Constructors">
    public DTOProcessorFollowFreeUserBatch(
            @NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache,
            @NotNull HeroListCache heroListCache,
            @NotNull GetPositionsCache getPositionsCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @NotNull AllowableRecipientPaginatedCache allowableRecipientPaginatedCache,
            @NotNull BatchFollowFormDTO followFormDTO)
    {
        super(userProfileCache, homeContentCache);
        this.heroListCache = heroListCache;
        this.getPositionsCache = getPositionsCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.allowableRecipientPaginatedCache = allowableRecipientPaginatedCache;
        this.followFormDTO = followFormDTO;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        UserBaseKey heroId;
        for (Integer userId : followFormDTO.userIds)
        {
            heroId = new UserBaseKey(userId);
            heroListCache.invalidate(heroId);
            getPositionsCache.invalidate(heroId);
            userMessagingRelationshipCache.markIsFreeHero(heroId);
        }
        allowableRecipientPaginatedCache.invalidateAll();
        return processed;
    }
}
