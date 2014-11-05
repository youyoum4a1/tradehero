package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTO;
import android.support.annotation.NonNull;

public class AllowableRecipientDTO implements DTO
{
    @NonNull public UserBaseDTO user;
    public UserMessagingRelationshipDTO relationship;
}
