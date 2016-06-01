package com.ayondo.academy.models.fastfill.jumio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ayondo.academy.models.fastfill.DocumentCheckStatus;
import com.ayondo.academy.models.fastfill.ScanStatus;
import java.util.Date;

public class NetverifyScanStatus implements ScanStatus
{
    @NonNull private final Date timestamp;
    @NonNull private final NetverifyScanReference scanReference;
    @NonNull private final NetverifyStatus status;

    public NetverifyScanStatus(
            @JsonProperty("timestamp") @NonNull Date timestamp,
            @JsonProperty("scanReference") @NonNull NetverifyScanReference scanReference,
            @JsonProperty("status") @NonNull NetverifyStatus status)
    {
        this.timestamp = timestamp;
        this.scanReference = scanReference;
        this.status = status;
    }

    @NonNull @Override public Date getTimestamp()
    {
        return timestamp;
    }

    @NonNull @Override public NetverifyScanReference getScanReference()
    {
        return scanReference;
    }

    @NonNull public NetverifyStatus getNetverifyStatus()
    {
        return status;
    }

    @NonNull @Override public DocumentCheckStatus getCheckStatus()
    {
        return status.checkStatus;
    }
}
