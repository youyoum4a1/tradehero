package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;


public class UserProfileRetrievedMilestone extends DTORetrievedAsyncMilestone<UserBaseKey, UserProfileDTO, UserProfileCache>
{
    public static final String TAG = UserProfileRetrievedMilestone.class.getSimpleName();

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
