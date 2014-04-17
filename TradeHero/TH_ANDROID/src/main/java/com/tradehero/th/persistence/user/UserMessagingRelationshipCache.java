package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserMessagingRelationshipCache extends StraightDTOCache<UserBaseKey, UserMessagingRelationshipDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public UserMessagingRelationshipCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserMessagingRelationshipDTO fetch(UserBaseKey key) throws Throwable
    {
        throw new IllegalStateException("You cannot call this fetch");
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
