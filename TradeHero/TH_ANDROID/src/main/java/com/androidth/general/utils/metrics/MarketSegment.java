package com.androidth.general.utils.metrics;

import android.support.annotation.NonNull;
import com.androidth.general.api.misc.DeviceType;

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
