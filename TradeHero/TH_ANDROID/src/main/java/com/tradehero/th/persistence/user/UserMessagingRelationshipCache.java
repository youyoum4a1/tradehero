package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.service.MessageServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserMessagingRelationshipCache extends StraightDTOCacheNew<UserBaseKey, UserMessagingRelationshipDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final MessageServiceWrapper messageServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserMessagingRelationshipCache(@NotNull MessageServiceWrapper messageServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.messageServiceWrapper = messageServiceWrapper;
    }
    //</editor-fold>

    @Override public UserMessagingRelationshipDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return messageServiceWrapper.getMessagingRelationgshipStatus(key);
    }
}
