package com.androidth.general.api.users;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;

public class AllowableRecipientDTO implements DTO
{
    @NonNull public UserBaseDTO user;
    public UserMessagingRelationshipDTO relationship;
}
