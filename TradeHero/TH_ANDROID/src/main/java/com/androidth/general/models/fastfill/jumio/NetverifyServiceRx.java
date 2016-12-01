package com.androidth.general.models.fastfill.jumio;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * https://www.jumio.com/implementation-guides/netverify-retrieval-api/
 */
public interface NetverifyServiceRx
{
    @GET("api/scans/{scanReference}")
    Observable<NetverifyScanStatus> getScanStatus(
            @Path("scanReference") String scanReference);

    @GET("api/scans/{scanReference}/images")
    Observable<NetverifyScanImagesDTO> getScanImages(
            @Path("scanReference") String scanReference);
}
