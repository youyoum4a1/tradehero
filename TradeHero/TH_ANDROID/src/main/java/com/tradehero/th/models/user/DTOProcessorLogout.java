package com.tradehero.th.models.user;

import android.app.NotificationManager;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.DTOCacheUtil;

public class DTOProcessorLogout  implements DTOProcessor<UserProfileDTO>
{
    private final DTOCacheUtil dtoCacheUtil;
    private final NotificationManager notificationManager;

    public DTOProcessorLogout(DTOCacheUtil dtoCacheUtil, NotificationManager notificationManager)
    {
        super();
        this.dtoCacheUtil = dtoCacheUtil;
        this.notificationManager = notificationManager;
    }

    @Override public UserProfileDTO process(UserProfileDTO userProfileDTO)
    {
        dtoCacheUtil.clearUserRelatedCaches();
        notificationManager.cancelAll();
        return userProfileDTO;
    }
}