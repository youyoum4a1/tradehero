package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class AllowableRecipientCutDTO implements DTO
{
    @NotNull public UserBaseDTO user;

    public AllowableRecipientCutDTO(
            @NotNull AllowableRecipientDTO allowableRecipientDTO,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache)
    {
        super();
        this.user = allowableRecipientDTO.user;
        userMessagingRelationshipCache.put(allowableRecipientDTO.user.getBaseKey(), allowableRecipientDTO.relationship);
    }

    @Nullable AllowableRecipientDTO inflate(
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache)
    {
        AllowableRecipientDTO inflated = new AllowableRecipientDTO();
        inflated.user = this.user;
        UserMessagingRelationshipDTO cachedRelationship = userMessagingRelationshipCache.get(user.getBaseKey());
        if (cachedRelationship == null)
        {
            return null;
        }
        inflated.relationship = cachedRelationship;
        return inflated;
    }
}
