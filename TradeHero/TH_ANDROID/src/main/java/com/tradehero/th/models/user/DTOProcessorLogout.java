package com.ayondo.academy.models.user;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.DTOProcessor;
import rx.functions.Action1;

public class DTOProcessorLogout implements DTOProcessor<UserProfileDTO>,
        Action1<UserProfileDTO>
{
    @NonNull private final DTOCacheUtilRx dtoCacheUtil;
    @NonNull private final NotificationManager notificationManager;

    //<editor-fold desc="Constructors">
    public DTOProcessorLogout(
            @NonNull DTOCacheUtilRx dtoCacheUtil,
            @NonNull NotificationManager notificationManager)
    {
        super();
        this.dtoCacheUtil = dtoCacheUtil;
        this.notificationManager = notificationManager;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(UserProfileDTO userProfileDTO)
    {
        dtoCacheUtil.clearUserCaches();
        notificationManager.cancelAll();
        return userProfileDTO;
    }

    @Override public void call(UserProfileDTO userProfileDTO)
    {
        process(userProfileDTO);
    }
}