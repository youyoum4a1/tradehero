package com.tradehero.th.network.service;

import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface PortfolioService
{
    //<editor-fold desc="Get User Portfolio List">
    @GET("/users/{userId}/portfolios")
    PortfolioCompactDTOList getPortfolios(
            @Path("userId") int userId,
            @Query("includeWatchlist") Boolean includeWatchList);
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    @GET("/users/{userId}/portfolios/{portfolioId}")
    PortfolioDTO getPortfolio(
            @Path("userId") int userId,
            @Path("portfolioId") int portfolioId);
    //</editor-fold>



    //GET https://tradehero.mobi/api/usercompetitions/293/portfolio HTTP/1.1
    //getPortfolioCompact
    //获取比赛 protfolioCompactDTO
    @GET("/usercompetitions/{competitionId}/portfolio")
    PortfolioDTO getPortfolioCompact(
            @Path("competitionId") int competitionId);
    //</editor-fold>
}
