package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.alert.DTOProcessorCreateAlert;
import com.tradehero.th.models.alert.DTOProcessorUpdateAlert;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class AlertServiceWrapper
{
    @NotNull private final AlertService alertService;
    @NotNull private final AlertServiceAsync alertServiceAsync;
    @NotNull private final Lazy<AlertCompactListCache> alertCompactListCache;
    @NotNull private final Lazy<AlertCompactCache> alertCompactCache;
    @NotNull private final Lazy<AlertCache> alertCache;

    @Inject public AlertServiceWrapper(
            @NotNull AlertService alertService,
            @NotNull AlertServiceAsync alertServiceAsync,
            @NotNull Lazy<AlertCompactListCache> alertCompactListCache,
            @NotNull Lazy<AlertCompactCache> alertCompactCache,
            @NotNull Lazy<AlertCache> alertCache)
    {
        super();
        this.alertService = alertService;
        this.alertServiceAsync = alertServiceAsync;
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

    @NotNull
    private DTOProcessor<AlertCompactDTO> createDTOProcessorCreateAlert(@NotNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorCreateAlert(
                userBaseKey,
                alertCompactListCache.get(),
                alertCompactCache.get(),
                alertCache.get());
    }

    @NotNull
    private DTOProcessor<AlertCompactDTO> createDTOProcessorUpdateAlert(@NotNull AlertId alertId)
    {
        return new DTOProcessorUpdateAlert(
                alertId,
                alertCompactCache.get(),
                alertCache.get());
    }

    //<editor-fold desc="Get Alerts">
    public AlertCompactDTOList getAlerts(@NotNull UserBaseKey userBaseKey)
    {
        basicCheck(userBaseKey);
        return alertService.getAlerts(userBaseKey.key);
    }

    public MiddleCallback<AlertCompactDTOList> getAlerts(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Callback<AlertCompactDTOList> callback)
    {
        basicCheck(userBaseKey);
        MiddleCallback<AlertCompactDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        alertServiceAsync.getAlerts(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

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

    public MiddleCallback<AlertDTO> getAlert(AlertId alertId, Callback<AlertDTO> callback)
    {
        MiddleCallback<AlertDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.alertServiceAsync.getAlert(alertId.userId, alertId.alertId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    public AlertCompactDTO createAlert(@NotNull UserBaseKey userBaseKey, @NotNull AlertFormDTO alertFormDTO)
    {
        basicCheck(userBaseKey);
        return createDTOProcessorCreateAlert(userBaseKey).process(this.alertService.createAlert(userBaseKey.key, alertFormDTO));
    }

    public MiddleCallback<AlertCompactDTO> createAlert(@NotNull UserBaseKey userBaseKey, @NotNull AlertFormDTO alertFormDTO, @Nullable Callback<AlertCompactDTO> callback)
    {
        basicCheck(userBaseKey);
        MiddleCallback<AlertCompactDTO> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorCreateAlert(userBaseKey));
        this.alertServiceAsync.createAlert(userBaseKey.key, alertFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    public AlertCompactDTO updateAlert(@NotNull AlertId alertId, @NotNull AlertFormDTO alertFormDTO)
    {
        basicCheck(alertId);
        return createDTOProcessorUpdateAlert(alertId).process(this.alertService.updateAlert(alertId.userId, alertId.alertId, alertFormDTO));
    }

    public MiddleCallback<AlertCompactDTO> updateAlert(@NotNull AlertId alertId, @NotNull AlertFormDTO alertFormDTO, @Nullable Callback<AlertCompactDTO> callback)
    {
        basicCheck(alertId);
        MiddleCallback<AlertCompactDTO> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorUpdateAlert(alertId));
        this.alertServiceAsync.updateAlert(alertId.userId, alertId.alertId, alertFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
