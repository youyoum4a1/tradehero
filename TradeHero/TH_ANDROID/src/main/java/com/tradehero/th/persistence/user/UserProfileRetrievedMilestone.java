package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestoneNew;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

@Deprecated // We should always ask for it
public class UserProfileRetrievedMilestone extends DTORetrievedAsyncMilestoneNew<UserBaseKey, UserProfileDTO, UserProfileCache>
{
    @Inject Lazy<UserProfileCache> userProfileCache;

    @Inject public UserProfileRetrievedMilestone(CurrentUserId currentUserId)
    {
        this(currentUserId.toUserBaseKey());
    }

    public UserProfileRetrievedMilestone(UserBaseKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override protected UserProfileCache getCache()
    {
        return userProfileCache.get();
    }

    @Override public void launch()
    {
        launchOwn();
    }
}
