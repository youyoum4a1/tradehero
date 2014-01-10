package com.tradehero.th.api.push;

/**
 * Created by xavier on 1/10/14.
 */
public interface PushNotificationManager
{
    void initialise();
    void enablePush();
    void disablePush();
    void setSoundEnabled(boolean enabled);
    void setVibrateEnabled(boolean enabled);
}
