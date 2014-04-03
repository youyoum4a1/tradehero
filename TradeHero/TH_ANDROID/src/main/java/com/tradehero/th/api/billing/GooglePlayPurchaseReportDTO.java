package com.tradehero.th.api.billing;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 11:31 AM To change this template use File | Settings | File Templates. */
public class GooglePlayPurchaseReportDTO implements PurchaseReportDTO
{
    public static final String DATA_JSON_KEY = "google_play_data";
    public static final String SIGNATURE_JSON_KEY = "google_play_signature";

    // Pass it back as the original JSON
    @JsonProperty(DATA_JSON_KEY)
    public final String googlePlayData;
    @JsonProperty(SIGNATURE_JSON_KEY)
    public final String googlePlaySignature;

    public GooglePlayPurchaseReportDTO(final String googlePlayData,
            final String googlePlaySignature)
    {
        this.googlePlayData = googlePlayData;
        this.googlePlaySignature = googlePlaySignature;
    }
}
