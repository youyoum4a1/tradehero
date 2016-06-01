package com.ayondo.academy.api.users;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;

public class AllowableRecipientDTO implements DTO
{
    @NonNull public UserBaseDTO user;
    public UserMessagingRelationshipDTO relationship;
}
