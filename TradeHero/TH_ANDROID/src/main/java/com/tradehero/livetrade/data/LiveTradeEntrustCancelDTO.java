package com.tradehero.livetrade.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengWithdrawEnterDTO;

import cn.htsec.data.pkg.trade.TradeDataHelper;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class LiveTradeEntrustCancelDTO {

    public String resultMsg;

    public static LiveTradeEntrustCancelDTO parseHaitongDTO(TradeDataHelper tradeDataHelper) {
        LiveTradeEntrustCancelDTO dto = new LiveTradeEntrustCancelDTO();
        dto.resultMsg = tradeDataHelper.getResultMsg();

        return dto;
    }

    public static LiveTradeEntrustCancelDTO parseHengshengDTO(HengshengWithdrawEnterDTO data) {
        LiveTradeEntrustCancelDTO dto = new LiveTradeEntrustCancelDTO();
        dto.resultMsg = "已提交";

        return dto;
    }
}
