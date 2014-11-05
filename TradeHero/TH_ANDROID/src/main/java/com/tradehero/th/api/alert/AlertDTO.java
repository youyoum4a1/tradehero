package com.tradehero.th.api.alert;

import java.util.List;
import android.support.annotation.Nullable;

public class AlertDTO extends AlertCompactDTO
{
    @Nullable public List<AlertEventDTO> alertEvents;

    //<editor-fold desc="Constructors">
    public AlertDTO()
    {
    }
    //</editor-fold>
}
