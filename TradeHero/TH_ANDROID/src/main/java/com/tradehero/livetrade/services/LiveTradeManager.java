package com.tradehero.livetrade.services;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Sam on 15/8/27.
 */
@Singleton public class LiveTradeManager {

    private LiveTradeServices liveTradeServices;

    @Inject public LiveTradeManager() {
        liveTradeServices = null;
    }

    public LiveTradeServices getLiveTradeServices() {
        return liveTradeServices;
    }

    public void setLiveTradeServices(LiveTradeServices liveTradeServices) {
        this.liveTradeServices = liveTradeServices;
    }
}
