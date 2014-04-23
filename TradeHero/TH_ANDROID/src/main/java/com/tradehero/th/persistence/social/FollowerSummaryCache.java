package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class FollowerSummaryCache extends StraightDTOCache<UserBaseKey, FollowerSummaryDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    protected FollowerServiceWrapper followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public FollowerSummaryCache(FollowerServiceWrapper followerServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.followerServiceWrapper = followerServiceWrapper;
    }
    //</editor-fold>

    @Override protected FollowerSummaryDTO fetch(UserBaseKey key) throws Throwable
    {
        return followerServiceWrapper.getAllFollowersSummary(key);
    }

    public List<FollowerSummaryDTO> getOrFetch(List<UserBaseKey> baseKeys) throws Throwable
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<FollowerSummaryDTO> followerSummaryDTOs = new ArrayList<>();
        for (UserBaseKey baseKey : baseKeys)
        {
            followerSummaryDTOs.add(getOrFetch(baseKey, false));
        }
        return followerSummaryDTOs;
    }
}
