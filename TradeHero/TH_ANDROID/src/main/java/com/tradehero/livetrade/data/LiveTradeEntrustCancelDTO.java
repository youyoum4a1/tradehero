package com.tradehero.livetrade.data;

import cn.htsec.data.pkg.trade.TradeDataHelper;

/**
 * Created by Sam on 15/8/27.
 */
public class LiveTradeEntrustCancelDTO {

    public String resultMsg;

    public static LiveTradeEntrustCancelDTO parseHaitongDTO(TradeDataHelper tradeDataHelper) {
        LiveTradeEntrustCancelDTO dto = new LiveTradeEntrustCancelDTO();
        dto.resultMsg = tradeDataHelper.getResultMsg();

        return dto;
    }
}
