package com.tradehero.th.models.push;

import android.app.Activity;
import android.support.annotation.NonNull;

public interface PushNotificationManager
{
    void initialise();
    void verify(@NonNull Activity activity);
    void enablePush();
    void disablePush();
    void setSoundEnabled(boolean enabled);
    void setVibrateEnabled(boolean enabled);
}
