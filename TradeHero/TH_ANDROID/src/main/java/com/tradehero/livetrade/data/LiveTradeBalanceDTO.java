package com.tradehero.livetrade.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengBalanceDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengBalanceData;

import cn.htsec.data.pkg.trade.TradeDataHelper;

/**
 * Created by Sam on 15/8/27.
 */
public class LiveTradeBalanceDTO {
    public float enableBalance;

    public static LiveTradeBalanceDTO parseHaitongDTO(TradeDataHelper helper) {
        int rowCount = helper.getRowCount();
        float enabledBalance = 0;
        for(int i = 0; i < rowCount; i++) {
            enabledBalance = helper.get(i, "enable_balance", enabledBalance);
            break;
        }

        LiveTradeBalanceDTO dto = new LiveTradeBalanceDTO();
        dto.enableBalance = enabledBalance;

        return dto;
    }

    public static LiveTradeBalanceDTO parseHengshengDTO(HengshengBalanceDTO data) {
        LiveTradeBalanceDTO dto = new LiveTradeBalanceDTO();
        HengshengBalanceData balanceData = data.data.get(0);
        dto.enableBalance = balanceData.enable_balance;

        return dto;
    }
}
