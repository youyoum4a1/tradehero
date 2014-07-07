package com.tradehero.th.models.security;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSecurityPosition implements DTOProcessor<SecurityPositionDetailDTO>
{
    @NotNull private final SecurityId securityId;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final SecurityPositionDetailCache securityPositionDetailCache;

    public DTOProcessorSecurityPosition(
            @NotNull SecurityPositionDetailCache securityPositionDetailCache,
            @NotNull UserProfileCache userProfileCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull SecurityId securityId)
    {
        this.securityId = securityId;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.securityPositionDetailCache = securityPositionDetailCache;
    }

    @Override public SecurityPositionDetailDTO process(SecurityPositionDetailDTO value)
    {
        securityPositionDetailCache.put(securityId, value);

        if (value.portfolio != null)
        {
            UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
            UserProfileDTO userProfileDTO = userProfileCache.get(userBaseKey);
            if (userProfileDTO != null && (userProfileDTO.portfolio == null || userProfileDTO.portfolio.id == value.portfolio.id))
            {
                userProfileDTO.portfolio = value.portfolio;
            }
        }
        return value;
    }
}
