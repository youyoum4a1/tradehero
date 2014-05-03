package com.tradehero.common.billing.googleplay;

import com.fasterxml.jackson.annotation.JsonProperty;


public class GooglePlayPurchaseDTO
{
    public static final String TAG = GooglePlayPurchaseDTO.class.getSimpleName();

    // Pass it back as the original JSON
    @JsonProperty("google_play_data")
    public final String googlePlayData;
    @JsonProperty("google_play_signature")
    public final String googlePlaySignature;

    public GooglePlayPurchaseDTO(final String googlePlayData, final String googlePlaySignature)
    {
        this.googlePlayData = googlePlayData;
        this.googlePlaySignature = googlePlaySignature;
    }
}
