package com.tradehero.livetrade.hengsheng;

import com.tradehero.livetrade.hengsheng.data.HengShengEntrustEnterDTO;
import com.tradehero.livetrade.hengsheng.data.HengshengBalanceDTO;
import com.tradehero.livetrade.hengsheng.data.HengshengBusinessQryDTO;
import com.tradehero.livetrade.hengsheng.data.HengshengEntrustQryDTO;
import com.tradehero.livetrade.hengsheng.data.HengshengPositionDTO;
import com.tradehero.livetrade.hengsheng.data.HengshengSessionDTO;
import com.tradehero.livetrade.hengsheng.data.HengshengWithdrawEnterDTO;
import com.tradehero.livetrade.hengsheng.services.HengshengRequestCallback;
import com.tradehero.livetrade.hengsheng.services.HengshengServiceAync;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Callback;


/**
 * Created by Sam on 15/8/24.
 */
@Singleton public class HengshengServicesAyncWrapper {

    @NotNull private HengshengManager hengshengManager;
    @NotNull private final HengshengServiceAync service;

    @Inject public HengshengServicesAyncWrapper(@NotNull HengshengServiceAync service,
                                                @NotNull HengshengManager hengshengManager) {
        this.service = service;
        this.hengshengManager = hengshengManager;
    }

    /**
     * 登录
     * @param targetBusinsys
     * @param opStation
     * @param inputContent
     * @param accountContent
     * @param password
     * @param cb
     */
    public void login(int targetBusinsys,
                             String opStation,
                             int inputContent,
                             String accountContent,
                             String password,
                             Callback<HengshengSessionDTO> cb)
    {
        service.login(HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                HengshengConstants.SENDER_COMP_ID,
                targetBusinsys,
                opStation,
                inputContent,
                accountContent,
                password,
                cb);
    }

    /**
     * 获取账号资金
     * @param cb
     */
    public void getBalance(HengshengRequestCallback<HengshengBalanceDTO> cb)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isAccessTokenValid())
        {
            service.getBalance(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    cb);
        }
        else
        {
            cb.sessionTimeout();
        }
    }

    /**
     * 获取仓位
     * @param cb
     */
    public void getPosition(HengshengRequestCallback<HengshengPositionDTO> cb)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isAccessTokenValid())
        {
            service.getPosition(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    cb);
        }
        else
        {
            cb.sessionTimeout();
        }
    }

    /**
     * 委托买入
     * @param exchangeType
     * @param stockCode
     * @param amount
     * @param price
     * @param cb
     */
    public void buy(int exchangeType, String stockCode, int amount, float price, HengshengRequestCallback<HengShengEntrustEnterDTO> cb)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isAccessTokenValid())
        {
            service.entrustEnter(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    exchangeType,
                    "",
                    stockCode,
                    amount,
                    price,
                    HengshengConstants.EXCHANGE_BUY,
                    0,
                    cb);
        }
        else
        {
            cb.sessionTimeout();
        }
    }

    /**
     * 委托卖出
     * @param exchangeType
     * @param stockCode
     * @param amount
     * @param price
     * @param cb
     */
    public void sell(int exchangeType, String stockCode, int amount, float price, HengshengRequestCallback<HengShengEntrustEnterDTO> cb)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isAccessTokenValid())
        {
            service.entrustEnter(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    exchangeType,
                    "",
                    stockCode,
                    amount,
                    price,
                    HengshengConstants.EXCHANGE_SELL,
                    0,
                    cb);
        }
        else
        {
            cb.sessionTimeout();
        }
    }

    /**
     * 委托查询
     * @param cb
     */
    public void entrustQuery(HengshengRequestCallback<HengshengEntrustQryDTO> cb)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isAccessTokenValid())
        {
            service.entrustQry(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    cb);
        }
        else
        {
            cb.sessionTimeout();
        }
    }

    /**
     * 撤销委托
     * @param entrustNo
     * @param cb
     */
    public void withdrawEnter(int entrustNo, HengshengRequestCallback<HengshengWithdrawEnterDTO> cb)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isAccessTokenValid())
        {
            service.withdrawEnter(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    entrustNo,
                    cb);
        }
        else
        {
            cb.sessionTimeout();
        }
    }

    /**
     * 当日成交
     * @param exchangeType
     * @param cb
     */
    public void businessQry(int exchangeType, HengshengRequestCallback<HengshengBusinessQryDTO> cb)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isAccessTokenValid())
        {
            service.businessQry(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    exchangeType,
                    cb);
        }
        else
        {
            cb.sessionTimeout();
        }
    }



    private boolean isAccessTokenValid()
    {
        String authStr = hengshengManager.getAccessToken();
        if (authStr.equals(HengshengManager.SESSION_TIME_OUT))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
