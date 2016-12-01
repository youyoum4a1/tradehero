package com.androidth.general.network.service;

import com.androidth.general.api.billing.PurchaseReportDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.users.UserProfileDTO;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface PortfolioServiceRx
{
    //<editor-fold desc="Get User Portfolio List">
    @GET("api/users/{userId}/portfolios")
    Observable<PortfolioCompactDTOList> getPortfolios(
            @Path("userId") int userId,
            @Query("includeWatchlist") Boolean includeWatchList);
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    @GET("api/users/{userId}/portfolios/{portfolioId}")
    Observable<PortfolioDTO> getPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId);
    //</editor-fold>

    //<editor-fold desc="Reset One User Portfolio">
    @POST("api/users/{userId}/portfolios/{portfolioId}/reset")
    Observable<UserProfileDTO> resetPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Body PurchaseReportDTO purchaseReportDTO);
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    @POST("api/users/{userId}/portfolios/{portfolioId}/addcash")
    Observable<UserProfileDTO> addCash(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Body PurchaseReportDTO purchaseReportDTO);
    //</editor-fold>
}
