package com.tradehero.th.network.service;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.purchase.PurchaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:11 PM To change this template use File | Settings | File Templates. */
public interface PortfolioService
{
    //<editor-fold desc="Get User Portfolio List">
    @GET("/users/{userId}/portfolios")
    List<PortfolioCompactDTO> getPortfolios(
            @Path("userId") int userId)
        throws RetrofitError;

    @GET("/users/{userId}/portfolios")
    void getPortfolios(
            @Path("userId") int userId,
            Callback<List<PortfolioCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    @GET("/users/{userId}/portfolios/{portfolioId}")
    PortfolioDTO getPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId)
        throws RetrofitError;

    @GET("/users/{userId}/portfolios/{portfolioId}")
    void getPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            Callback<PortfolioDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Reset One User Portfolio">
    @POST("/users/{userId}/portfolios/{portfolioId}/reset")
    UserProfileDTO resetPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Body PurchaseDTO purchaseDTO)
        throws RetrofitError;

    @POST("/users/{userId}/portfolios/{portfolioId}/reset")
    void resetPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Body PurchaseDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    @POST("/users/{userId}/portfolios/{portfolioId}/addcash")
    UserProfileDTO addCash(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Body PurchaseDTO purchaseDTO)
        throws RetrofitError;

    @POST("/users/{userId}/portfolios/{portfolioId}/addcash")
    void addCash(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            @Body PurchaseDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Mark One User Portfolio">
    @POST("/users/{userId}/portfolios/{portfolioId}/mark")
    PortfolioDTO markPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId)
        throws RetrofitError;

    @POST("/users/{userId}/portfolios/{portfolioId}/mark")
    void markPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId,
            Callback<PortfolioDTO> callback);
    //</editor-fold>
}
