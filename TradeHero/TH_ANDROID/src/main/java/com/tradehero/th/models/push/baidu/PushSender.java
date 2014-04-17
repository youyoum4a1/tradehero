package com.tradehero.th.models.push.baidu;

import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.prefs.BaiduPushDeviceIdentifierSentFlag;
import com.tradehero.th.persistence.prefs.SavedBaiduPushDeviceIdentifier;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by wangliang on 14-4-16.
 */
public class PushSender
{
    @Inject Lazy<SessionServiceWrapper> sessionServiceWrapper;
    @Inject CurrentUserId currentUserId;
    @Inject @SavedBaiduPushDeviceIdentifier StringPreference savedPushDeviceIdentifier;
    @Inject @BaiduPushDeviceIdentifierSentFlag BooleanPreference pushDeviceIdentifierSentFlag;

    public PushSender()
    {
        DaggerUtils.inject(this);
    }

    public void updateDeviceIdentifier(String appId,String userId, String channelId)
    {
        if (currentUserId == null)
        {
            Timber.e("Current user is null, quit");
            return;
        }
        if (pushDeviceIdentifierSentFlag.get())
        {
            Timber.d("Already send the device token to the server, quit");
            return;
        }
        //save the device token
        BaiduDeviceMode deviceMode = new BaiduDeviceMode(channelId,userId,appId);
        savedPushDeviceIdentifier.set(deviceMode.token);
        Timber.d("save the token %s",deviceMode.token);
        Timber.d("get saved the token %s",savedPushDeviceIdentifier.get());
        sessionServiceWrapper.get().updateDevice(deviceMode,new UpdateDeviceIdentifierCallback());
    }

    class UpdateDeviceIdentifierCallback implements Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            pushDeviceIdentifierSentFlag.set(true);
        }

        @Override public void failure(RetrofitError error)
        {
            pushDeviceIdentifierSentFlag.set(false);
        }
    }
}
