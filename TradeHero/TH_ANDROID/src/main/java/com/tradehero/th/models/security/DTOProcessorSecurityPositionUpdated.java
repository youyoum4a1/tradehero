package com.tradehero.th.models.security;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSecurityPositionUpdated extends DTOProcessorSecurityPositionReceived
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final SecurityPositionDetailCache securityPositionDetailCache;

    public DTOProcessorSecurityPositionUpdated(
            @NotNull SecurityPositionDetailCache securityPositionDetailCache,
            @NotNull UserProfileCache userProfileCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull SecurityId securityId)
    {
        super(securityId, currentUserId);
        this.userProfileCache = userProfileCache;
        this.securityPositionDetailCache = securityPositionDetailCache;
    }

    @Override public SecurityPositionDetailDTO process(@NotNull SecurityPositionDetailDTO value)
    {
        value = super.process(value);
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
