package com.tradehero.firmbargain.hengsheng.services;


import com.tradehero.firmbargain.hengsheng.HengshengConstants;
import com.tradehero.firmbargain.hengsheng.data.HengShengEntrustEnterDTO;
import com.tradehero.firmbargain.hengsheng.data.HengshengBalanceDTO;
import com.tradehero.firmbargain.hengsheng.data.HengshengBusinessQryDTO;
import com.tradehero.firmbargain.hengsheng.data.HengshengEntrustQryDTO;
import com.tradehero.firmbargain.hengsheng.data.HengshengPositionDTO;
import com.tradehero.firmbargain.hengsheng.data.HengshengSessionDTO;
import com.tradehero.firmbargain.hengsheng.data.HengshengWithdrawEnterDTO;


import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface HengshengServiceAync
{
    /**
     * Login API
     * @param targetComp
     * @param senderComp
     * @param targetBusinsys
     * @param opStation
     * @param inputContent
     * @param accountContent
     * @param password
     * @param cb
     */

    @POST("/oauth2/oauth2/oauthacct_trade_bind")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
            "Authorization: " + HengshengConstants.BASIC
    })
    void login(
            @Field("targetcomp_id") int targetComp,
            @Field("sendercomp_id") int senderComp,
            @Field("targetbusinsys_no") int targetBusinsys,
            @Field("op_station") String opStation,
            @Field("input_content") int inputContent,
            @Field("account_content") String accountContent,
            @Field("password") String password,
            Callback<HengshengSessionDTO> cb
    );


    /**
     * Get the balance.
     * @param authorization
     * @param targetComp
     * @param senderComp
     * @param cb
     */
    @POST("/secu/v1/balancefast_qry")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
    })
    void getBalance(
            @Header("Authorization") String authorization,
            @Field("targetcomp_id") int targetComp,
            @Field("sendercomp_id") int senderComp,
            HengshengRequestCallback<HengshengBalanceDTO> cb
    );


    /**
     * Get the current position.
     * @param authorization
     * @param targetComp
     * @param senderComp
     * @param cb
     */
    @POST("/secu/v1/stockposition_qry")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
    })
    void getPosition(
            @Header("Authorization") String authorization,
            @Field("targetcomp_id") int targetComp,
            @Field("sendercomp_id") int senderComp,
            HengshengRequestCallback<HengshengPositionDTO> cb
    );


    /**
     * Buy and sell
     * @param authorization
     * @param targetComp
     * @param senderComp
     * @param exchangeType
     * @param stockCode
     * @param amount
     * @param price
     * @param entrustBs   value can be {@link HengshengConstants.EXCHANGE_BUY} or {@link HengshengConstants.EXCHANGE_SELL}
     * @param entrustProp
     * @param cb
     */
    @POST("/secu/v1/entrust_enter")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
    })
    void entrustEnter(
            @Header("Authorization") String authorization,
            @Field("targetcomp_id") int targetComp,
            @Field("sendercomp_id") int senderComp,
            @Field("exchange_type") int exchangeType,
            @Field("stock_account") String stockAcount,
            @Field("stock_code") String stockCode,
            @Field("entrust_amount") int amount,
            @Field("entrust_price") float price,
            @Field("entrust_bs") int entrustBs,
            @Field("entrust_prop") int entrustProp,
            HengshengRequestCallback<HengShengEntrustEnterDTO> cb
    );


    /**
     * Entrust query
     * @param authorization
     * @param targetComp
     * @param senderComp
     * @param cb
     */
    @POST("/secu/v1/entrust_qry")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
    })
    void entrustQry(
            @Header("Authorization") String authorization,
            @Field("targetcomp_id") int targetComp,
            @Field("sendercomp_id") int senderComp,
            HengshengRequestCallback<HengshengEntrustQryDTO> cb
    );


    /**
     * Withdraw enter
     * @param authorization
     * @param targetComp
     * @param senderComp
     * @param entrustNo
     * @param cb
     */
    @POST("/secu/v1/withdraw_enter")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
    })
    void withdrawEnter(
            @Header("Authorization") String authorization,
            @Field("targetcomp_id") int targetComp,
            @Field("sendercomp_id") int senderComp,
            @Field("entrust_no") int entrustNo,
            HengshengRequestCallback<HengshengWithdrawEnterDTO> cb
    );

    /**
     * Business query
     * @param authorization
     * @param targetComp
     * @param senderComp
     * @param exchangeType
     * @param cb
     */
    @POST("/secu/v1/business_qry")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
    })
    void businessQry(
            @Header("Authorization") String authorization,
            @Field("targetcomp_id") int targetComp,
            @Field("sendercomp_id") int senderComp,
            @Field("exchange_type") int exchangeType,
            HengshengRequestCallback<HengshengBusinessQryDTO> cb
    );
}
