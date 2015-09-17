package com.tradehero.livetrade.services;

import com.tradehero.livetrade.thirdPartyServices.haitong.HaitongServicesWrapper;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.HengshengServicesWrapper;
import com.tradehero.livetrade.thirdPartyServices.xincaitong.XCTServicesWrapper;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
@Singleton public class LiveTradeManager {

    private LiveTradeServices liveTradeServices;
    @Inject HaitongServicesWrapper haitongServicesWrapper;
    @Inject HengshengServicesWrapper hengshengServicesWrapper;
    @Inject XCTServicesWrapper xctServicesWrapper;

    @Inject public LiveTradeManager() {

    }

    public LiveTradeServices getLiveTradeServices() {
        //Todo
        return xctServicesWrapper;
    }

    public void setLiveTradeServices(LiveTradeServices liveTradeServices) {
        this.liveTradeServices = liveTradeServices;
    }
}
