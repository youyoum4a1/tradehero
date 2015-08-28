package com.tradehero.livetrade.data;

import cn.htsec.data.pkg.trade.TradeDataHelper;

/**
 * Created by Sam on 15/8/27.
 */
public class LiveTradeEntrustEnterDTO {

    public String resultMsg;

    public static LiveTradeEntrustEnterDTO parseHaitongDTO(TradeDataHelper tradeDataHelper) {
        LiveTradeEntrustEnterDTO dto = new LiveTradeEntrustEnterDTO();
        dto.resultMsg = tradeDataHelper.getResultMsg();

        return dto;
    }
}
