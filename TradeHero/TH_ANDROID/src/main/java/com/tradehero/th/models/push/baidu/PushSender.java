package com.tradehero.th.models.push.baidu;

import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.prefs.BaiduPushDeviceIdentifierSentFlag;
import com.tradehero.th.persistence.prefs.SavedBaiduPushDeviceIdentifier;
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
    private final Lazy<SessionServiceWrapper> sessionServiceWrapper;
    private final CurrentUserId currentUserId;
    private final @SavedBaiduPushDeviceIdentifier StringPreference savedPushDeviceIdentifier;
    private final @BaiduPushDeviceIdentifierSentFlag BooleanPreference pushDeviceIdentifierSentFlag;

    @Inject public PushSender(
            Lazy<SessionServiceWrapper> sessionServiceWrapper,
            CurrentUserId currentUserId,
            @SavedBaiduPushDeviceIdentifier StringPreference savedPushDeviceIdentifier,
            @BaiduPushDeviceIdentifierSentFlag BooleanPreference pushDeviceIdentifierSentFlag)
    {
        this.sessionServiceWrapper = sessionServiceWrapper;
        this.currentUserId = currentUserId;
        this.savedPushDeviceIdentifier = savedPushDeviceIdentifier;
        this.pushDeviceIdentifierSentFlag = pushDeviceIdentifierSentFlag;
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

    public void setPushDeviceIdentifierSentFlag(boolean bind)
    {
        pushDeviceIdentifierSentFlag.set(bind);
    }

    class UpdateDeviceIdentifierCallback implements Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            Timber.d("UpdateDeviceIdentifierCallback send success");
            setPushDeviceIdentifierSentFlag(true);
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.e(error,"UpdateDeviceIdentifierCallback send failure");
            setPushDeviceIdentifierSentFlag(false);
        }
    }
}
