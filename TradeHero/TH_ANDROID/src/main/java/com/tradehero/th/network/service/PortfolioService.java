package com.tradehero.th.network.service;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:11 PM To change this template use File | Settings | File Templates. */
public interface PortfolioService
{
    //<editor-fold desc="Get Portfolio List">
    @GET("/users/{userId}/portfolios")
    List<PortfolioCompactDTO> getPortfolios(
            @Path("userId") int userId)
        throws RetrofitError;

    @GET("/users/{userId}/portfolios")
    void getPortfolios(
            @Path("userId") int userId,
            Callback<List<PortfolioCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Portfolio">
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
}
