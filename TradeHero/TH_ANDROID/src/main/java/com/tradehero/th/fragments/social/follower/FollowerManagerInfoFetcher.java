package com.tradehero.th.fragments.social.follower;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class FollowerManagerInfoFetcher
{
    @Inject protected FollowerSummaryCache followerSummaryCache;
    private DTOCache.GetOrFetchTask<UserBaseKey, FollowerSummaryDTO> followerSummaryFetchTask;
    private final DTOCache.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener;

    public FollowerManagerInfoFetcher(final DTOCache.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener)
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
        if (this.followerSummaryFetchTask != null)
        {
            this.followerSummaryFetchTask.setListener(null);
        }
        this.followerSummaryFetchTask = null;
    }

    public void fetch(final UserBaseKey heroId)
    {
        detachFetchTask();
        this.followerSummaryFetchTask = this.followerSummaryCache.getOrFetch(heroId, this.followerSummaryListener);
        this.followerSummaryFetchTask.execute();
    }

    /**
     *
     * @param heroId
     * @param followerSummaryListener
     */
    public void fetch(final UserBaseKey heroId,DTOCache.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener)
    {
        detachFetchTask();
        this.followerSummaryFetchTask = this.followerSummaryCache.getOrFetch(heroId, true, followerSummaryListener);
        this.followerSummaryFetchTask.execute();
    }
}
