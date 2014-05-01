package com.tradehero.th.network.service;

import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

interface PortfolioServiceAsync
{
    //<editor-fold desc="Get User Portfolio List">
    @GET("/users/{userId}/portfolios")
    void getPortfolios(
            @Path("userId") int userId,
            @Query("includeWatchlist") Boolean includeWatchList,
            Callback<List<PortfolioCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    @GET("/users/{userId}/portfolios/{portfolioId}")
    void getPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            Callback<PortfolioDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Reset One User Portfolio">
    @POST("/users/{userId}/portfolios/{portfolioId}/reset")
    void resetPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Body PurchaseReportDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    @POST("/users/{userId}/portfolios/{portfolioId}/addcash")
    void addCash(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Body PurchaseReportDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Mark One User Portfolio">
    @POST("/users/{userId}/portfolios/{portfolioId}/mark")
    void markPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            Callback<PortfolioDTO> callback);
    //</editor-fold>
}
