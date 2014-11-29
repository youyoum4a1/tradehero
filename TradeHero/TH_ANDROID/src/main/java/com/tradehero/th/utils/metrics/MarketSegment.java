package com.tradehero.th.utils.metrics;

import android.support.annotation.NonNull;
import com.tradehero.th.api.misc.DeviceType;

public enum MarketSegment
{
    CHINA(DeviceType.ChineseVersion),
    ROW(DeviceType.Android), // Rest Of World
    ;

    @NonNull public final DeviceType deviceType;

    MarketSegment(@NonNull DeviceType deviceType)
    {
        this.deviceType = deviceType;
    }
}
