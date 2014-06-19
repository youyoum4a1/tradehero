package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateAlert implements DTOProcessor<AlertCompactDTO>
{
    @NotNull private final AlertId alertId;
    @NotNull private final AlertCompactCache alertCompactCache;
    @NotNull private final AlertCache alertCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateAlert(@NotNull AlertId alertId, @NotNull AlertCompactCache alertCompactCache,
            @NotNull AlertCache alertCache)
    {
        this.alertId = alertId;
        this.alertCompactCache = alertCompactCache;
        this.alertCache = alertCache;
    }
    //</editor-fold>

    @Override public AlertCompactDTO process(AlertCompactDTO alertCompactDTO)
    {
        alertCompactCache.put(alertId, alertCompactDTO);
        alertCache.invalidate(alertId);
        return alertCompactDTO;
    }
}
