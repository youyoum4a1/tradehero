package com.tradehero.livetrade.services;

import com.tradehero.livetrade.data.LiveTradeBalanceDTO;
import com.tradehero.livetrade.data.LiveTradeDealQueryDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustCancelDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustEnterDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePositionDTO;
import com.tradehero.livetrade.data.LiveTradeSessionDTO;

/**
 * Created by Sam on 15/8/27.
 */
public interface LiveTradeServices {

    boolean isSessionValid();

    void login(String account, String password, LiveTradeCallback<LiveTradeSessionDTO> callback);

    void logout();

    void signup();

    void getBalance(LiveTradeCallback<LiveTradeBalanceDTO> callback) ;

    void getPosition(LiveTradeCallback<LiveTradePositionDTO> callback);

    void buy(int exchangeType, String stockCode, int amount, float price, LiveTradeCallback<LiveTradeEntrustEnterDTO> callback);

    void sell(int exchangeType, String stockCode, int amount, float price, LiveTradeCallback<LiveTradeEntrustEnterDTO> callback);

    void entrustQuery(LiveTradeCallback<LiveTradeEntrustQueryDTO> callback);

    void entrustCancel(int entrustNo, LiveTradeCallback<LiveTradeEntrustCancelDTO> callback);

    void dealQuery(LiveTradeCallback<LiveTradeDealQueryDTO> callback);

}
