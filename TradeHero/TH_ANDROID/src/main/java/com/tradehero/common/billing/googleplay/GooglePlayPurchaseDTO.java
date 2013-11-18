package com.tradehero.common.billing.googleplay;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 11:31 AM To change this template use File | Settings | File Templates. */
public class GooglePlayPurchaseDTO
{
    public static final String TAG = GooglePlayPurchaseDTO.class.getSimpleName();

    // Pass it back as the original JSON
    public final String google_play_data;
    public final String google_play_signature;

    public GooglePlayPurchaseDTO(final String google_play_data, final String google_play_signature)
    {
        this.google_play_data = google_play_data;
        this.google_play_signature = google_play_signature;
    }
}
