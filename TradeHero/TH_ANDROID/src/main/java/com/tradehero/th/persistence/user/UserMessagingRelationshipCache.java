package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.service.MessageServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserMessagingRelationshipCache extends StraightDTOCache<UserBaseKey, UserMessagingRelationshipDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    private final MessageServiceWrapper messageServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserMessagingRelationshipCache(MessageServiceWrapper messageServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.messageServiceWrapper = messageServiceWrapper;
    }
    //</editor-fold>

    @Override protected UserMessagingRelationshipDTO fetch(UserBaseKey key) throws Throwable
    {
        return messageServiceWrapper.getMessagingRelationgshipStatus(key);
    }

    public List<UserMessagingRelationshipDTO> get(List<UserBaseKey> baseKeys)
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserMessagingRelationshipDTO> userMessagingRelationshipDTOs = new ArrayList<>();
        for (UserBaseKey baseKey: baseKeys)
        {
            userMessagingRelationshipDTOs.add(get(baseKey));
        }
        return userMessagingRelationshipDTOs;
    }
}
