package com.androidth.general.models.fastfill.jumio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetverifyScanImageShortDTO
{
    @NonNull private final NetverifyImageClassifier classifier;
    @NonNull private final String href;
    @Nullable private final NetverifyMaskHint maskHint;

    public NetverifyScanImageShortDTO(
            @JsonProperty("classifier") @NonNull NetverifyImageClassifier classifier,
            @JsonProperty("href") @NonNull String href,
            @JsonProperty("maskhint") @Nullable NetverifyMaskHint maskHint)
    {
        this.classifier = classifier;
        this.href = href;
        this.maskHint = maskHint;
    }

    @NonNull public NetverifyImageClassifier getClassifier()
    {
        return classifier;
    }

    @NonNull public String getHref()
    {
        return href;
    }

    @Nullable public NetverifyMaskHint getMaskHint()
    {
        return maskHint;
    }
}
