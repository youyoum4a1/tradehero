package com.tradehero.th.models.user;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import rx.functions.Action1;

public class DTOProcessorLogout implements DTOProcessor<UserProfileDTO>,
        Action1<UserProfileDTO>
{
    @NonNull private final DTOCacheUtilNew dtoCacheUtilNew;
    @NonNull private final DTOCacheUtilRx dtoCacheUtilRx;
    @NonNull private final NotificationManager notificationManager;

    //<editor-fold desc="Constructors">
    public DTOProcessorLogout(
            @NonNull DTOCacheUtilNew dtoCacheUtilNew,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull NotificationManager notificationManager)
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

    @Override public void call(UserProfileDTO userProfileDTO)
    {
        process(userProfileDTO);
    }
}