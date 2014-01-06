package com.tradehero.th.fragments.social.follower;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by xavier on 12/16/13.
 */
public class FollowerManagerInfoFetcher
{
    public static final String TAG = FollowerManagerInfoFetcher.class.getSimpleName();

    @Inject protected Lazy<FollowerSummaryCache> followerSummaryCache;
    private DTOCache.GetOrFetchTask<UserBaseKey, FollowerSummaryDTO> followerSummaryFetchTask;
    private final DTOCache.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener;

    public FollowerManagerInfoFetcher(final DTOCache.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener)
    {
        super();
        this.followerSummaryListener = followerSummaryListener;
        DaggerUtils.inject(this);
    }

    public void onPause()
    {
        if (this.followerSummaryFetchTask != null)
        {
            this.followerSummaryFetchTask.setListener(null);
        }
        this.followerSummaryFetchTask = null;
    }

    public void fetch(final UserBaseKey followedId)
    {
        FollowerSummaryDTO summaryDTO = this.followerSummaryCache.get().get(followedId);
        if (summaryDTO != null)
        {
            if (this.followerSummaryListener != null)
            {
                this.followerSummaryListener.onDTOReceived(followedId, summaryDTO);
            }
        }
        else
        {
            if (this.followerSummaryFetchTask != null)
            {
                this.followerSummaryFetchTask.setListener(null);
            }
            this.followerSummaryFetchTask = this.followerSummaryCache.get().getOrFetch(followedId, this.followerSummaryListener);
            this.followerSummaryFetchTask.execute();
        }
    }
}
