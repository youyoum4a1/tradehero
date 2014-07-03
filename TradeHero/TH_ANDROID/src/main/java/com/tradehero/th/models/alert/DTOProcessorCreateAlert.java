package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorCreateAlert implements DTOProcessor<AlertCompactDTO>
{
    @NotNull private final UserBaseKey userBaseKey;
    @NotNull private final AlertCompactListCache alertCompactListCache;
    @NotNull private final AlertCompactCache alertCompactCache;
    @NotNull private final AlertCache alertCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorCreateAlert(
            @NotNull UserBaseKey userBaseKey,
            @NotNull AlertCompactListCache alertCompactListCache,
            @NotNull AlertCompactCache alertCompactCache,
            @NotNull AlertCache alertCache)
    {
        this.userBaseKey = userBaseKey;
        this.alertCompactListCache = alertCompactListCache;
        this.alertCompactCache = alertCompactCache;
        this.alertCache = alertCache;
    }
    //</editor-fold>

    @Override public AlertCompactDTO process(AlertCompactDTO alertCompactDTO)
    {
        AlertId alertId = alertCompactDTO.getAlertId(userBaseKey);
        alertCompactListCache.invalidate(userBaseKey);
        alertCompactCache.put(alertId, alertCompactDTO);
        alertCache.invalidate(alertId);
        return alertCompactDTO;
    }
}
