package com.tradehero.th.models.user;

import android.app.NotificationManager;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorLogout implements DTOProcessor<UserProfileDTO>
{
    @NotNull private final DTOCacheUtilNew dtoCacheUtilNew;
    @NotNull private final DTOCacheUtilRx dtoCacheUtilRx;
    @NotNull private final NotificationManager notificationManager;

    //<editor-fold desc="Constructors">
    public DTOProcessorLogout(
            @NotNull DTOCacheUtilNew dtoCacheUtilNew,
            @NotNull DTOCacheUtilRx dtoCacheUtilRx,
            @NotNull NotificationManager notificationManager)
    {
        super();
        this.dtoCacheUtilNew = dtoCacheUtilNew;
        this.dtoCacheUtilRx = dtoCacheUtilRx;
        this.notificationManager = notificationManager;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(UserProfileDTO userProfileDTO)
    {
        dtoCacheUtilNew.clearUserCaches();
        dtoCacheUtilRx.clearUserCaches();
        notificationManager.cancelAll();
        return userProfileDTO;
    }
}