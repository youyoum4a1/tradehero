package com.tradehero.th.models.push;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import javax.inject.Inject;

public class EmptyPushNotificationManager implements PushNotificationManager
{
    //<editor-fold desc="Constructors">
    @Inject public EmptyPushNotificationManager()
    {
    }
    //</editor-fold>

    @Override public void initialise()
    {
        THToast.show("Initialise not implemented");
    }

    @Override public void verify(@NonNull Activity activity)
    {
        THToast.show("Verify not implemented");
    }

    @Override public void enablePush()
    {
        THToast.show("EnablePush not implemented");
    }

    @Override public void disablePush()
    {
        THToast.show("DisablePush not implemented");
    }

    @Override public void setSoundEnabled(boolean enabled)
    {
        THToast.show("SetSoundEnabled not implemented");
    }

    @Override public void setVibrateEnabled(boolean enabled)
    {
        THToast.show("SetVibrateEnabled not implemented");
    }
}
