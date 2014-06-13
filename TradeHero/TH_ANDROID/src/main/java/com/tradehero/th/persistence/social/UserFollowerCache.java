package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserFollowerCache extends StraightDTOCache<FollowerHeroRelationId, UserFollowerDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<FollowerServiceWrapper> followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserFollowerCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserFollowerDTO fetch(FollowerHeroRelationId key) throws Throwable
    {
        return this.followerServiceWrapper.get().getFollowerSubscriptionDetail(key);
    }

    public List<UserFollowerDTO> getOrFetch(List<FollowerHeroRelationId> followerHeroRelationIds) throws Throwable
    {
        if (followerHeroRelationIds == null)
        {
            return null;
        }

        List<UserFollowerDTO> followerSummaryDTOs = new ArrayList<>();
        for (FollowerHeroRelationId baseKey: followerHeroRelationIds)
        {
            followerSummaryDTOs.add(getOrFetch(baseKey, false));
        }
        return followerSummaryDTOs;
    }
}
