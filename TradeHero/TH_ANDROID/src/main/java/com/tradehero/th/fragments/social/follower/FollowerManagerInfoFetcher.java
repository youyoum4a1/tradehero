package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import javax.inject.Inject;

public class FollowerManagerInfoFetcher
{
    @Inject protected FollowerSummaryCache followerSummaryCache;
    private final DTOCacheNew.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener;

    public FollowerManagerInfoFetcher(Context context, final DTOCacheNew.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener)
    {
        super();
        this.followerSummaryListener = followerSummaryListener;
        HierarchyInjector.inject(context, this);
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
        fetch(heroId,false);
    }

    public void fetch(final UserBaseKey heroId,boolean forceUpdate)
    {
        detachFetchTask();
        this.followerSummaryCache.register(heroId, this.followerSummaryListener);
        this.followerSummaryCache.getOrFetchAsync(heroId,forceUpdate);
    }
}
