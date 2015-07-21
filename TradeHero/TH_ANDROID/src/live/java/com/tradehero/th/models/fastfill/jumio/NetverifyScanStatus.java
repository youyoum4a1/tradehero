package com.tradehero.th.models.fastfill.jumio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class NetverifyScanStatus
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

    @NonNull public Date getTimestamp()
    {
        return timestamp;
    }

    @NonNull public NetverifyScanReference getScanReference()
    {
        return scanReference;
    }

    @NonNull public NetverifyStatus getStatus()
    {
        return status;
    }
}
