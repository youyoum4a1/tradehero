package com.tradehero.livetrade.data;


import java.util.ArrayList;
import java.util.List;

import cn.htsec.data.pkg.trade.TradeDataHelper;
import timber.log.Timber;


/**
 * Created by Sam on 15/8/27.
 */
public class LiveTradeEntrustQueryDTO {

    public List<EntrustQueryDTO> positions;

    public LiveTradeEntrustQueryDTO() {
        this.positions = new ArrayList();
    }

    public static LiveTradeEntrustQueryDTO parseHaitongData(TradeDataHelper helper) {
        LiveTradeEntrustQueryDTO dtos = new LiveTradeEntrustQueryDTO();

        int rowCount = helper.getRowCount();
        int responseCode = helper.getResponseCode();
        String responseMsg = helper.getResponseMsg();

        int resultCode = helper.getResultCode();
        String resultMsg = helper.getResultMsg();

        int startPosition = helper.getStartPosition();

        StringBuffer sb = new StringBuffer();
        sb.append("响应Code:" + responseCode + "\n");
        sb.append("响应Msg:" + responseMsg + "\n");
        sb.append("结果Code:" + resultCode + "\n");
        sb.append("结果Msg:" + resultMsg + "\n");
        sb.append("起始位置:" + startPosition + "\n");
        sb.append("结果行数:" + rowCount + "\n");

        List<String> keys = helper.getKeys();
        for (int i = 0; i < rowCount; i++) {
            sb.append(i + ",");
            EntrustQueryDTO dto = new EntrustQueryDTO();
            for (int j = 0; j < keys.size(); j++) {
                String key = keys.get(j);
                sb.append(key + ":" + helper.get(i, key, null));
                if (j != keys.size() - 1) {
                    sb.append("  ");
                }
                if (key.equalsIgnoreCase("sec_name")) {
                    dto.securityName = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("sec_code")) {
                    dto.securityId = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_name")) {
                    dto.entrustName = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_price")) {
                    dto.entrustPrice = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_amt")) {
                    dto.entrustAmount = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_date")) {
                    dto.entrustDate = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_time")) {
                    dto.entrustTime = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_status_name")) {
                    dto.entrustStatusName = helper.get(i, key, null);
                }
            }
            sb.append("\n");
            dtos.positions.add(dto);
        }

        Timber.d("lyl " + sb.toString());
        return dtos;
    }
}

class EntrustQueryDTO {
    public String securityName;
    public String securityId;
    public String entrustName;
    public String entrustPrice;
    public String entrustAmount;
    public String entrustDate;
    public String entrustTime;
    public String entrustStatusName;
}
