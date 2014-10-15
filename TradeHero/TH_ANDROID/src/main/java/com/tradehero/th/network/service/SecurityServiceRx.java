package com.tradehero.th.network.service;

import com.tradehero.th.api.security.SecurityCompactDTOList;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface SecurityServiceRx
{
    //<editor-fold desc="Get Basic Trending">
    @GET("/securities/trending/")
    Observable<SecurityCompactDTOList> getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>
}
