package com.tradehero.th.models.fastfill.jumio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.models.fastfill.ScanImageKey;

public class NetverifyScanImageKey implements ScanImageKey
{
    @NonNull private final NetverifyScanReference scanReference;
    @NonNull private final NetverifyImageClassifier classifier;
    @Nullable private final NetverifyMaskHint maskHint;

    public NetverifyScanImageKey(
            @JsonProperty("scanReference") @NonNull NetverifyScanReference scanReference,
            @JsonProperty("classifier") @NonNull NetverifyImageClassifier classifier,
            @JsonProperty("maskhint") @Nullable NetverifyMaskHint maskHint)
    {
        this.scanReference = scanReference;
        this.classifier = classifier;
        this.maskHint = maskHint;
    }

    @NonNull @Override public NetverifyScanReference getScanReference()
    {
        return scanReference;
    }

    @NonNull public NetverifyImageClassifier getClassifier()
    {
        return classifier;
    }

    @Nullable public NetverifyMaskHint getMaskHint()
    {
        return maskHint;
    }
}
