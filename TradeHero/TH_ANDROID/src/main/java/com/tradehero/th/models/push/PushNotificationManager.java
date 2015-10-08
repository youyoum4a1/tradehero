package com.tradehero.th.models.push;

import android.app.Activity;
import android.support.annotation.NonNull;
import rx.Observable;

public interface PushNotificationManager
{
    @NonNull Observable<InitialisationCompleteDTO> initialise();
    void verify(@NonNull Activity activity);
    void enablePush();
    void disablePush();
    void setSoundEnabled(boolean enabled);
    void setVibrateEnabled(boolean enabled);
    String getChannelId();


    public static class InitialisationCompleteDTO
    {
        @NonNull public final String pushId;

        public InitialisationCompleteDTO(@NonNull String pushId)
        {
            this.pushId = pushId;
        }
    }
}
