package com.tradehero.th.models.user;

import android.app.NotificationManager;
import android.content.Context;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.user.UserProfileCache;

public class DTOProcessorLogout extends DTOProcessorUpdateUserProfile
{
    private final DTOCacheUtil dtoCacheUtil;
    private final Context context;

    public DTOProcessorLogout(UserProfileCache userProfileCache,
            DTOCacheUtil dtoCacheUtil, Context context)
    {
        super(userProfileCache);
        this.dtoCacheUtil = dtoCacheUtil;
        this.context = context;
    }

    @Override public UserProfileDTO process(UserProfileDTO userProfileDTO)
    {
        dtoCacheUtil.clearUserRelatedCaches();
        //TODO also has to clear notification
        clearNotification();
        return super.process(userProfileDTO);
    }

    public void clearNotification()
    {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }
}