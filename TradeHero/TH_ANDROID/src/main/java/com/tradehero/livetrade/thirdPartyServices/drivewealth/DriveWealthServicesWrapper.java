package com.tradehero.livetrade.thirdPartyServices.drivewealth;

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
import com.tradehero.livetrade.services.LiveTradeServices;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.services.DriveWealthServiceAync;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
@Singleton public class DriveWealthServicesWrapper implements LiveTradeServices {

    private DriveWealthServiceAync mServices;
    private DriveWealthManager mManager;

    @Inject public DriveWealthServicesWrapper(@NotNull DriveWealthServiceAync service,
                                              @NotNull DriveWealthManager manager) {
        mServices = service;
        mManager = manager;
    }

    @Override
    public boolean needCheckPhoneNumber() {
        return false;
    }

    @Override
    public boolean isSessionValid() {
        return false;
    }

    @Override
    public void login(Activity activity, String account, String password, LiveTradeCallback<LiveTradeSessionDTO> callback) {

    }

    @Override
    public void logout() {

    }

    @Override
    public void signup(Activity activity) {

    }

    @Override
    public void getBalance(LiveTradeCallback<LiveTradeBalanceDTO> callback) {

    }

    @Override
    public void getPosition(LiveTradeCallback<LiveTradePositionDTO> callback) {

    }

    @Override
    public void buy(String stockCode, int amount, float price, LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {

    }

    @Override
    public void sell(String stockCode, int amount, float price, LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {

    }

    @Override
    public void pendingEntrustQuery(LiveTradeCallback<LiveTradePendingEntrustQueryDTO> callback) {

    }

    @Override
    public void entrustQuery(LiveTradeCallback<LiveTradeEntrustQueryDTO> callback) {

    }

    @Override
    public void entrustCancel(String marketCode, String entrustNo, String entrustDate, String withdrawCate, String securityId, LiveTradeCallback<LiveTradeEntrustCancelDTO> callback) {

    }

    @Override
    public void dealQuery(LiveTradeCallback<LiveTradeDealQueryDTO> callback) {

    }

    public void signupFree() {
        DriveWealthSignupFormDTO formDTO = mManager.getSignupFormDTO();


    }
}
