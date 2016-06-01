package com.ayondo.academy.utils.metrics;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.misc.DeviceType;

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
