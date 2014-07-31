package com.tradehero.th.api.alert;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class AlertDTO extends AlertCompactDTO
{
    @Nullable public List<AlertEventDTO> alertEvents;

    //<editor-fold desc="Constructors">
    public AlertDTO()
    {
    }
    //</editor-fold>
}
