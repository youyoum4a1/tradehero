package com.tradehero.th.models.push;

public interface PushNotificationManager
{
    void initialise();
    void enablePush();
    void disablePush();
    void setSoundEnabled(boolean enabled);
    void setVibrateEnabled(boolean enabled);
}
