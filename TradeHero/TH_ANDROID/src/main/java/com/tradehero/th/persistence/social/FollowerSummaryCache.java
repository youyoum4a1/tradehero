package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.FollowerService;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class FollowerSummaryCache extends StraightDTOCache<UserBaseKey, FollowerSummaryDTO>
{
    public static final String TAG = FollowerSummaryCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<FollowerService> followerService;

    //<editor-fold desc="Constructors">
    @Inject public FollowerSummaryCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected FollowerSummaryDTO fetch(UserBaseKey key) throws Throwable
    {
        return followerService.get().getFollowersSummary(key.key);
    }

    public List<FollowerSummaryDTO> getOrFetch(List<UserBaseKey> baseKeys) throws Throwable
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<FollowerSummaryDTO> followerSummaryDTOs = new ArrayList<>();
        for (UserBaseKey baseKey: baseKeys)
        {
            followerSummaryDTOs.add(getOrFetch(baseKey, false));
        }
        return followerSummaryDTOs;
    }
}
