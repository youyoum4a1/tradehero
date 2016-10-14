package com.androidth.general.network.service;


import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.api.live1b.*;
import com.androidth.general.api.portfolio.OwnedPortfolioIdList;
import com.androidth.general.api.position.PositionDTOList;
import com.androidth.general.api.position.SecurityPositionTransactionDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.api.security.TransactionFormDTO;

import java.util.Map;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface Live1BServiceRx {

    //<editor-fold desc="Get Multiple Securities">
    @GET("/securities/multi/")
    Observable<Map<Integer, SecurityCompactDTO>> getMultipleSecurities(
            @Query("securityIds") String commaSeparatedIntegerIds);
    //</editor-fold>

    //<editor-fold desc="Get Basic Trending">
    @GET("/securities/trending/")
    Observable<SecurityCompactDTOList> getTrendingSecurities(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending By Volume">
    @GET("/securities/trendingVol/")
    Observable<SecurityCompactDTOList> getTrendingSecuritiesByVolume(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>
// TODO doesn't exist yet in backend
    //<editor-fold desc="Get Trending By Price">
    @GET("/securities/trendingPrice/")
    Observable<SecurityCompactDTOList> getTrendingSecuritiesByPrice(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get Trending For All">
    @GET("/securities/trendingExchange/")
    Observable<SecurityCompactDTOList> getTrendingSecuritiesAllInExchange(
            @Query("exchange") String exchange,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Securities">
    @GET("/securities/search")
    Observable<SecurityCompactDTOList> searchSecurities(
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get List By Sector and Exchange">
    @GET("/securities/bySectorAndExchange")
    Observable<SecurityCompactDTOList> getBySectorAndExchange(
            @Query("exchange") Integer exchangeId,
            @Query("sector") Integer sectorId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/securities/bySectorsAndExchanges")
    Observable<SecurityCompactDTOList> getBySectorsAndExchanges(
            @Query("exchanges") String commaSeparatedExchangeIds,
            @Query("sectors") String commaSeparatedSectorIds,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>


    @GET("/securities/positions")
    Observable<PositionDTOList> getPositions(
            @Query("exch") String exchange,
            @Query("symbol") String securitySymbol);

    @GET("/securities/compact")
    Observable<SecurityCompactDTO> getCompactSecurity(
            @Query("exch") String exchange,
            @Query("symbol") String securitySymbol);



    @GET("/liveTradingSituation")
    Observable<LiveTradingSituationDTO> getLiveTradingSituation();

    @GET("/{validateURL}")
    Observable<Boolean>validateData(
            @Path("validateURL") String validateURL
    );

    @GET("/om/Positions")
    Observable<LiveTradingSituationDTO> getPositions();

    @POST("/securities/{exchange}/{securitySymbol}/buy")
    Observable<SecurityPositionTransactionDTO> buy(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);

    @POST("/securities/{exchange}/{securitySymbol}/sell")
    Observable<SecurityPositionTransactionDTO> sell(
            @Path("exchange") String exchange,
            @Path("securitySymbol") String securitySymbol,
            @Body() TransactionFormDTO transactionFormDTO);

    @POST("/om/NewOrder")
    Observable<PositionTransactionDTO> newOrder(
            @Body() NewOrderSingleDTO newOrderSingleDTO);

    @POST("/om/PaymentTransferId/{PaymentTransferIdRequestDTO}")
    Observable<PaymentTransferIdRequestDTO> getPaymentTransferId(
            @Path("PaymentTransferIdRequestDTO") PaymentTransferIdRequestDTO paymentTransferIdRequestDTO
    );
}
