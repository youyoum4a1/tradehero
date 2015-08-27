package com.tradehero.livetrade.haitong;

import com.tradehero.th.activities.CurrentActivityHolder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import cn.htsec.data.pkg.trade.IPackageProxy;
import cn.htsec.data.pkg.trade.TradeInterface;
import cn.htsec.data.pkg.trade.TradeManager;

/**
 * Created by Sam on 15/8/26.
 */
@Singleton
public class HaitongServicesWrapper {

    @NotNull protected final CurrentActivityHolder currentActivityHolder;
    private TradeManager tradeManager;

    @Inject public HaitongServicesWrapper(@NotNull CurrentActivityHolder currentActivityHolder) {
        this.currentActivityHolder = currentActivityHolder;
        tradeManager = TradeManager.getInstance(currentActivityHolder.getCurrentContext());
    }

    public void login() {

    }

    public void logout() {
        tradeManager.logout();
    }

    public boolean isSessionValid() {
        return tradeManager.isLogined();
    }

    public void getBalance(IPackageProxy proxy) {
        tradeManager.sendData(TradeInterface.ID_QUERY_MONEY_BALANCE, proxy);
    }

    public void getPosition(IPackageProxy proxy) {
        tradeManager.sendData(TradeInterface.ID_QUERY_POSITION, proxy);
    }

    public void entrustEnter(IPackageProxy proxy) {
        tradeManager.sendData(TradeInterface.ID_ENTRUST, proxy);
    }

    public void queryOrders(IPackageProxy proxy) {
        tradeManager.sendData(TradeInterface.ID_QUERY_ORDERS, proxy);
    }

    public void cancelOrders(IPackageProxy proxy) {
        tradeManager.sendData(TradeInterface.ID_CANCEL, proxy);
    }

    public void queryBargains(IPackageProxy proxy) {
        tradeManager.sendData(TradeInterface.ID_QUERY_BARGAINS, proxy);
    }
}
