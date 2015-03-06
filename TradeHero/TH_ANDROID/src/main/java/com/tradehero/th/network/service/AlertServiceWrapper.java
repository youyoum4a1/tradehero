package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class AlertServiceWrapper
{
    @NotNull private final AlertService alertService;
    @NotNull private final Lazy<AlertCompactListCache> alertCompactListCache;
    @NotNull private final Lazy<AlertCompactCache> alertCompactCache;
    @NotNull private final Lazy<AlertCache> alertCache;

    @Inject public AlertServiceWrapper(
            @NotNull AlertService alertService,
            @NotNull Lazy<AlertCompactListCache> alertCompactListCache,
            @NotNull Lazy<AlertCompactCache> alertCompactCache,
            @NotNull Lazy<AlertCache> alertCache)
    {
        super();
        this.alertService = alertService;
        this.alertCompactListCache = alertCompactListCache;
        this.alertCompactCache = alertCompactCache;
        this.alertCache = alertCache;
    }

    private void basicCheck(@NotNull UserBaseKey userBaseKey)
    {
        if (userBaseKey.key == null)
        {
            throw new NullPointerException("userBaseKey.key cannot be null");
        }
    }


    //<editor-fold desc="Get Alerts">
    public AlertCompactDTOList getAlerts(@NotNull UserBaseKey userBaseKey)
    {
        basicCheck(userBaseKey);
        return alertService.getAlerts(userBaseKey.key);
    }


    private void basicCheck(@NotNull AlertId alertId)
    {
        if (alertId.userId == null)
        {
            throw new NullPointerException("alertId.userId cannot be null");
        }
        if (alertId.alertId == null)
        {
            throw new NullPointerException("alertId.alertId cannot be null");
        }
    }

    //<editor-fold desc="Get Alert">
    public AlertDTO getAlert(@NotNull AlertId alertId)
    {
        basicCheck(alertId);
        return this.alertService.getAlert(alertId.userId, alertId.alertId);
    }


}
