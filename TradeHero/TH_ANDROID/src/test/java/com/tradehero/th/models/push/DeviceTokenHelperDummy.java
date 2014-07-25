package com.tradehero.th.models.push;

import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class DeviceTokenHelperDummy extends DeviceTokenHelper
{
    //<editor-fold desc="Constructors">
    @Inject public DeviceTokenHelperDummy(
            @NotNull @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier,
            @NotNull Context context)
    {
        super(savedPushDeviceIdentifier, context);
    }
    //</editor-fold>

    @Override public String getDeviceToken()
    {
        return "dummy";
    }
}
