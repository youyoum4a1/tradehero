package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserRelationDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserRelationCache extends StraightDTOCache<UserBaseKey, UserRelationDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public UserRelationCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserRelationDTO fetch(UserBaseKey key) throws Throwable
    {
        throw new IllegalStateException("You cannot call this fetch");
    }

    public List<UserRelationDTO> get(List<UserBaseKey> baseKeys)
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserRelationDTO> userRelationDTOs = new ArrayList<>();
        for (UserBaseKey baseKey: baseKeys)
        {
            userRelationDTOs.add(get(baseKey));
        }
        return userRelationDTOs;
    }
}
