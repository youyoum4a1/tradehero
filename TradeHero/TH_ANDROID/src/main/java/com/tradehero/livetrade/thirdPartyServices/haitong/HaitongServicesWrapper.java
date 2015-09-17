package com.tradehero.livetrade.thirdPartyServices.haitong;

import android.app.Activity;

import com.tradehero.livetrade.data.LiveTradeBalanceDTO;
import com.tradehero.livetrade.data.LiveTradeDealQueryDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustCancelDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustEnterDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePendingEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePositionDTO;
import com.tradehero.livetrade.data.LiveTradeSessionDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeConstants;
import com.tradehero.livetrade.services.LiveTradeServices;
import com.tradehero.th.activities.CurrentActivityHolder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import cn.htsec.data.SecAccountInfo;
import cn.htsec.data.pkg.trade.IPackageProxy;
import cn.htsec.data.pkg.trade.TradeDataHelper;
import cn.htsec.data.pkg.trade.TradeInterface;
import cn.htsec.data.pkg.trade.TradeManager;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
@Singleton
public class HaitongServicesWrapper implements LiveTradeServices {

    @NotNull protected final CurrentActivityHolder currentActivityHolder;
    private TradeManager tradeManager;

    @Inject public HaitongServicesWrapper(@NotNull CurrentActivityHolder currentActivityHolder) {
        this.currentActivityHolder = currentActivityHolder;
        tradeManager = TradeManager.getInstance(currentActivityHolder.getCurrentContext());
    }

    @Override
    public void login(Activity activity, String account, String password, LiveTradeCallback<LiveTradeSessionDTO> callback) {
        HaitongUtils.jumpToLoginHAITONG(activity);
    }

    @Override
    public void logout() {
        tradeManager.logout();
    }

    @Override
    public boolean isSessionValid() {
        return tradeManager.isLogined();
    }

    @Override
    public void signup(Activity activity) {
        HaitongUtils.openAnAccount(activity);
    }

    @Override
    public void getBalance(final LiveTradeCallback<LiveTradeBalanceDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);

                LiveTradeBalanceDTO dto = LiveTradeBalanceDTO.parseHaitongDTO(tradeDataHelper);
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_MONEY_BALANCE, proxy);
    }

    @Override
    public void getPosition(final LiveTradeCallback<LiveTradePositionDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onSend(TradeDataHelper helper) {
                helper.setStartPosition(0);
            }

            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradePositionDTO dto = LiveTradePositionDTO.parseHaitongDTO(tradeDataHelper);
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_POSITION, proxy);
    }

    @Override
    public void buy(final String stockCode, final int amount, final float price, final LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onSend(TradeDataHelper helper) {
                helper.set(TradeInterface.KEY_MARKET_CODE, HaitongUtils.getMarketCodeBySymbol(stockCode));
                helper.set(TradeInterface.KEY_ENTRUST_TYPE, "1");
                SecAccountInfo secAccountInfo = tradeManager.getSecAccounts().get(0);
                helper.set(TradeInterface.KEY_SEC_ACCOUNT, secAccountInfo.getAccount());
                helper.set(TradeInterface.KEY_SEC_CODE, stockCode);
                helper.set(TradeInterface.KEY_ENTRUST_PRICE, String.valueOf(price));
                helper.set(TradeInterface.KEY_ENTRUST_AMT, String.valueOf(amount));
                helper.set(TradeInterface.KEY_MARKET_ORDER_TYPE, "");
            }


            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeEntrustEnterDTO dto = LiveTradeEntrustEnterDTO.parseHaitongDTO(tradeDataHelper);
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_ENTRUST, proxy);
    }

    @Override
    public void sell(final String stockCode, final int amount, final float price, final LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onSend(TradeDataHelper helper) {
                helper.set(TradeInterface.KEY_MARKET_CODE, HaitongUtils.getMarketCodeBySymbol(stockCode));
                helper.set(TradeInterface.KEY_ENTRUST_TYPE, "2");
                SecAccountInfo secAccountInfo = tradeManager.getSecAccounts().get(0);
                helper.set(TradeInterface.KEY_SEC_ACCOUNT, secAccountInfo.getAccount());
                helper.set(TradeInterface.KEY_SEC_CODE, stockCode);
                helper.set(TradeInterface.KEY_ENTRUST_PRICE, String.valueOf(price));
                helper.set(TradeInterface.KEY_ENTRUST_AMT, String.valueOf(amount));
                helper.set(TradeInterface.KEY_MARKET_ORDER_TYPE, "");
            }

            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeEntrustEnterDTO dto = LiveTradeEntrustEnterDTO.parseHaitongDTO(tradeDataHelper);
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_ENTRUST, proxy);
    }

    @Override
    public void pendingEntrustQuery(final LiveTradeCallback<LiveTradePendingEntrustQueryDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onSend(TradeDataHelper helper) {
                helper.set(TradeInterface.KEY_WITHDRAW_FLAG, "1");
            }

            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradePendingEntrustQueryDTO dto = LiveTradePendingEntrustQueryDTO.parseHaitongData(tradeDataHelper);
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_ORDERS, proxy);
    }

    @Override
    public void entrustQuery(final LiveTradeCallback<LiveTradeEntrustQueryDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onSend(TradeDataHelper helper) {
                helper.set(TradeInterface.KEY_WITHDRAW_FLAG, "0");
            }

            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeEntrustQueryDTO dto = LiveTradeEntrustQueryDTO.parseHaitongData(tradeDataHelper);
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_ORDERS, proxy);
    }

    @Override
    public void entrustCancel(final String marketCode, final String entrustNo, final String entrustDate, final String withdrawCate, final String securityId, final LiveTradeCallback<LiveTradeEntrustCancelDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onSend(TradeDataHelper helper) {
                helper.set(TradeInterface.KEY_MARKET_CODE, marketCode);
                helper.set(TradeInterface.KEY_ENTRUST_DATE, entrustDate);
                helper.set(TradeInterface.KEY_WITHDRAW_CATE, withdrawCate);
                helper.set(TradeInterface.KEY_ENTRUST_NO, entrustNo);
                helper.set(TradeInterface.KEY_SEC_CODE, securityId);
            }

            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeEntrustCancelDTO dto = LiveTradeEntrustCancelDTO.parseHaitongDTO(tradeDataHelper);
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_CANCEL, proxy);
    }

    @Override
    public void dealQuery(final LiveTradeCallback<LiveTradeDealQueryDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeDealQueryDTO dto = LiveTradeDealQueryDTO.parseHaitiongData(tradeDataHelper);
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_BARGAINS, proxy);
    }
}
