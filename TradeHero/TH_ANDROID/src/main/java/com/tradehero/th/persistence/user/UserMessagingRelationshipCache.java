package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.service.MessageServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserMessagingRelationshipCache extends StraightDTOCache<UserBaseKey, UserMessagingRelationshipDTO>
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

    @Override protected UserMessagingRelationshipDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return messageServiceWrapper.getMessagingRelationgshipStatus(key);
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<UserMessagingRelationshipDTO> get(@Nullable List<UserBaseKey> baseKeys)
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserMessagingRelationshipDTO> userMessagingRelationshipDTOs = new ArrayList<>();
        for (@NotNull UserBaseKey baseKey: baseKeys)
        {
            userMessagingRelationshipDTOs.add(get(baseKey));
        }
        return userMessagingRelationshipDTOs;
    }
}
