package com.tradehero.livetrade.data;


import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengShengEntrustEnterDTO;

import cn.htsec.data.pkg.trade.TradeDataHelper;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class LiveTradeEntrustEnterDTO {

    public String resultMsg;

    public static LiveTradeEntrustEnterDTO parseHaitongDTO(TradeDataHelper tradeDataHelper) {
        LiveTradeEntrustEnterDTO dto = new LiveTradeEntrustEnterDTO();
        dto.resultMsg = tradeDataHelper.getResultMsg();

        return dto;
    }

    public static LiveTradeEntrustEnterDTO parseHengshengDTO(HengShengEntrustEnterDTO data) {
        LiveTradeEntrustEnterDTO dto = new LiveTradeEntrustEnterDTO();
        dto.resultMsg = "委托成功";

        return dto;
    }
}
