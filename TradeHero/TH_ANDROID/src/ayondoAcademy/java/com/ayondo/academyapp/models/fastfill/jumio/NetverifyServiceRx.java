package com.ayondo.academyapp.models.fastfill.jumio;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * https://www.jumio.com/implementation-guides/netverify-retrieval-api/
 */
public interface NetverifyServiceRx
{
    @GET("/scans/{scanReference}")
    Observable<NetverifyScanStatus> getScanStatus(
            @Path("scanReference") String scanReference);

    @GET("/scans/{scanReference}/images")
    Observable<NetverifyScanImagesDTO> getScanImages(
            @Path("scanReference") String scanReference);
}
