package com.tradehero.livetrade.thirdPartyServices.haitong;

import com.tradehero.livetrade.data.LiveTradeBalanceDTO;
import com.tradehero.livetrade.data.LiveTradeDealQueryDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustCancelDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustEnterDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePositionDTO;
import com.tradehero.livetrade.data.LiveTradeSessionDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeConstants;
import com.tradehero.livetrade.services.LiveTradeServices;
import com.tradehero.th.activities.CurrentActivityHolder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import cn.htsec.data.pkg.trade.IPackageProxy;
import cn.htsec.data.pkg.trade.TradeDataHelper;
import cn.htsec.data.pkg.trade.TradeInterface;
import cn.htsec.data.pkg.trade.TradeManager;

/**
 * Created by Sam on 15/8/26.
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
    public void login(String account, String password, LiveTradeCallback<LiveTradeSessionDTO> callback) {

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
    public void signup() {

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
                LiveTradeBalanceDTO dto = new LiveTradeBalanceDTO();
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_MONEY_BALANCE, proxy);
    }

    @Override
    public void getPosition(final LiveTradeCallback<LiveTradePositionDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradePositionDTO dto = new LiveTradePositionDTO();
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_POSITION, proxy);
    }

    @Override
    public void buy(int exchangeType, String stockCode, int amount, float price, final LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeEntrustEnterDTO dto = new LiveTradeEntrustEnterDTO();
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_ENTRUST, proxy);
    }

    @Override
    public void sell(int exchangeType, String stockCode, int amount, float price, final LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeEntrustEnterDTO dto = new LiveTradeEntrustEnterDTO();
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_ENTRUST, proxy);
    }

    @Override
    public void entrustQuery(final LiveTradeCallback<LiveTradeEntrustQueryDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeEntrustQueryDTO dto = new LiveTradeEntrustQueryDTO();
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_ORDERS, proxy);
    }

    @Override
    public void entrustCancel(int entrustNo, final LiveTradeCallback<LiveTradeEntrustCancelDTO> callback) {
        IPackageProxy proxy = new IPackageProxy(){
            @Override
            public void onRequestFail(String s) {
                super.onRequestFail(s);
                callback.onError(LiveTradeConstants.ERROR_CODE_HAITONG, s);
            }

            @Override
            public void onReceive(TradeDataHelper tradeDataHelper) {
                super.onReceive(tradeDataHelper);
                LiveTradeEntrustCancelDTO dto = new LiveTradeEntrustCancelDTO();
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
                LiveTradeDealQueryDTO dto = new LiveTradeDealQueryDTO();
                callback.onSuccess(dto);
            }

        };
        tradeManager.sendData(TradeInterface.ID_QUERY_BARGAINS, proxy);
    }
}
