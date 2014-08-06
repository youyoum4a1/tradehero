package com.tradehero.th.models.user;

import android.app.NotificationManager;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.DTOCacheUtil;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorLogout implements DTOProcessor<UserProfileDTO>
{
    @NotNull private final DTOCacheUtil dtoCacheUtil;
    @NotNull private final NotificationManager notificationManager;

    //<editor-fold desc="Constructors">
    public DTOProcessorLogout(
            @NotNull DTOCacheUtil dtoCacheUtil,
            @NotNull NotificationManager notificationManager)
    {
        super();
        this.dtoCacheUtil = dtoCacheUtil;
        this.notificationManager = notificationManager;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(UserProfileDTO userProfileDTO)
    {
        dtoCacheUtil.clearUserRelatedCaches();
        notificationManager.cancelAll();
        return userProfileDTO;
    }
}