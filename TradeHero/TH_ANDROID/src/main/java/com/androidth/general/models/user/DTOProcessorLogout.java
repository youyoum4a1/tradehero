package com.androidth.general.models.user;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.DTOProcessor;
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