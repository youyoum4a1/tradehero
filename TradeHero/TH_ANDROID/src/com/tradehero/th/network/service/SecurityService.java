package com.tradehero.th.network.service;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: xavier Date: 9/4/13 Time: 5:50 PM To change this template use File | Settings | File Templates. */
public interface SecurityService
{
    @GET("/securities/trending/")
    void getTrendingSecurities(Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/trending/")
    List<SecurityCompactDTO> getTrendingSecurities();

    @GET("/securities/search")
    void searchSecurities(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<SecurityCompactDTO>> callback);

    @GET("/securities/search")
    List<SecurityCompactDTO> searchSecurities(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage);

    @GET("/securities/{exchange}/{securitySymbol}")
    SecurityPositionDetailDTO getSecurity(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol);

    @GET("/securities/{exchange}/{securitySymbol}")
    void getSecurity(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            Callback<SecurityPositionDetailDTO> callback);

}
