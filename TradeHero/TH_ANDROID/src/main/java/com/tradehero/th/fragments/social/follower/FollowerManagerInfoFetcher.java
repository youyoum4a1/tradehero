package com.tradehero.th.fragments.social.follower;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class FollowerManagerInfoFetcher
{
    @Inject protected FollowerSummaryCache followerSummaryCache;
    private final DTOCacheNew.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener;

    public FollowerManagerInfoFetcher(final DTOCacheNew.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener)
    {
        super();
        this.followerSummaryListener = followerSummaryListener;
        DaggerUtils.inject(this);
    }

    public void onDestroyView()
    {
        detachFetchTask();
    }

    protected void detachFetchTask()
    {
        followerSummaryCache.unregister(followerSummaryListener);
    }

    public void fetch(final UserBaseKey heroId)
    {
        detachFetchTask();
        this.followerSummaryCache.register(heroId, this.followerSummaryListener);
        this.followerSummaryCache.getOrFetchAsync(heroId);
    }

    /**
     *
     * @param heroId
     * @param followerSummaryListener
     */
    public void fetch(final UserBaseKey heroId,DTOCacheNew.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener)
    {
        followerSummaryCache.unregister(followerSummaryListener);
        followerSummaryCache.register(heroId, followerSummaryListener);
        this.followerSummaryCache.getOrFetchAsync(heroId, true);
    }
}
