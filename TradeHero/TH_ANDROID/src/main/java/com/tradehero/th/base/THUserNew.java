package com.tradehero.th.base;

import android.content.SharedPreferences;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.prefs.ForDeviceToken;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class THUserNew
{
    protected Lazy<SharedPreferences> sharedPreferences;
    protected Lazy<UserServiceWrapper> userServiceWrapper;
    protected Lazy<SessionServiceWrapper> sessionServiceWrapper;
    protected Lazy<UserProfileCache> userProfileCache;
    protected Lazy<DTOCacheUtil> dtoCacheUtil;
    protected Lazy<AlertDialogUtil> alertDialogUtil;
    protected Lazy<CurrentActivityHolder> currentActivityHolder;
    protected @ForDeviceToken String deviceToken;

    @Inject public THUserNew(Lazy<SharedPreferences> sharedPreferences,
            Lazy<UserServiceWrapper> userServiceWrapper,
            Lazy<SessionServiceWrapper> sessionServiceWrapper,
            Lazy<UserProfileCache> userProfileCache,
            Lazy<DTOCacheUtil> dtoCacheUtil,
            Lazy<AlertDialogUtil> alertDialogUtil,
            Lazy<CurrentActivityHolder> currentActivityHolder,
            @ForDeviceToken String deviceToken)
    {
        super();
        this.sharedPreferences = sharedPreferences;
        this.userServiceWrapper = userServiceWrapper;
        this.sessionServiceWrapper = sessionServiceWrapper;
        this.userProfileCache = userProfileCache;
        this.dtoCacheUtil = dtoCacheUtil;
        this.alertDialogUtil = alertDialogUtil;
        this.currentActivityHolder = currentActivityHolder;
        this.deviceToken = deviceToken;
    }
}
