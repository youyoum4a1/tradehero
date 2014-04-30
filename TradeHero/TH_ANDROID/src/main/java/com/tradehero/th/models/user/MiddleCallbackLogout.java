package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier on 3/5/14.
 */
public class MiddleCallbackLogout extends MiddleCallback<UserProfileDTO>
{
    public static final String TAG = MiddleCallbackLogout.class.getSimpleName();

    @Inject DTOCacheUtil dtoCacheUtil;

    public MiddleCallbackLogout(Callback<UserProfileDTO> primaryCallback)
    {
        super(primaryCallback);
        DaggerUtils.inject(this);
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        super.success(userProfileDTO, response);
        dtoCacheUtil.clearUserRelatedCaches();
        //TODO also has to clear notification
    }

    //public void clearNotification()
    //{
    //    NotificationManager nm = (NotificationManager) context
    //            .getSystemService(Context.NOTIFICATION_SERVICE);
    //    nm.cancelAll();
    //}
}
