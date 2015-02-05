package com.tradehero.th.models.user;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import rx.functions.Action1;

public class DTOProcessorLogout implements DTOProcessor<UserProfileDTO>,
        Action1<UserProfileDTO>
{
    @NonNull private final DTOCacheUtilImpl dtoCacheUtil;
    @NonNull private final NotificationManager notificationManager;

    //<editor-fold desc="Constructors">
    public DTOProcessorLogout(
            @NonNull DTOCacheUtilImpl dtoCacheUtil,
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