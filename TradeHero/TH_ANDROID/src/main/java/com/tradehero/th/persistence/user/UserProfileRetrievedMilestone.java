package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.DTORetrievedMilestone;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:13 PM To change this template use File | Settings | File Templates. */
public class UserProfileRetrievedMilestone extends DTORetrievedMilestone<UserBaseKey, UserProfileDTO, UserProfileCache>
{
    public static final String TAG = UserProfileRetrievedMilestone.class.getSimpleName();

    @Inject Lazy<UserProfileCache> userProfileCache;

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
