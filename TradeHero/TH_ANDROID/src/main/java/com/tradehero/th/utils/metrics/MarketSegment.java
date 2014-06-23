package com.tradehero.th.utils.metrics;

import com.tradehero.th.api.misc.DeviceType;
import org.jetbrains.annotations.NotNull;

public enum MarketSegment
{
    CHINA(DeviceType.ChineseVersion),
    ROW(DeviceType.Android), // Rest Of World
    ;

    @NotNull public final DeviceType deviceType;

    MarketSegment(@NotNull DeviceType deviceType)
    {
        this.deviceType = deviceType;
    }
}
