package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;

public class AllowableRecipientDTO implements DTO
{
    @NotNull public UserBaseDTO user;
    public UserMessagingRelationshipDTO relationship;
}
