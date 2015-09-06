package com.tradehero.livetrade.data;


import com.tradehero.livetrade.data.subData.EntrustQueryDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengEntrustQryDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengEntrustQryData;

import java.util.ArrayList;
import java.util.List;

import cn.htsec.data.pkg.trade.TradeDataHelper;
import timber.log.Timber;


/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class LiveTradeEntrustQueryDTO {

    public static final int ENTRUST_STATUS_UNKNOWN = -1;
    public static final int ENTRUST_STATUS_DEALED = 0;
    public static final int ENTRUST_STATUS_UNDEALED = 1;
    public static final int ENTRUST_STATUS_WITHDRAWED = 2;

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
                    dto.entrustPrice = helper.get(i, key, 0.0f);
                } else if (key.equalsIgnoreCase("entrust_amt")) {
                    dto.entrustAmount = helper.get(i, key, 0);
                } else if (key.equalsIgnoreCase("entrust_date")) {
                    dto.entrustDate = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_time")) {
                    dto.entrustTime = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_status_name")) {
                    dto.entrustStatus = parseHaitongEntrustStatusID(helper.get(i, key, null));
                }
            }
            sb.append("\n");
            dtos.positions.add(dto);
        }

        Timber.d("lyl " + sb.toString());
        return dtos;
    }

    private static int parseHaitongEntrustStatusID(String name) {
        if (name == null) {
            return ENTRUST_STATUS_UNKNOWN;
        }

        if (name.equalsIgnoreCase("场外撤单") || name.equalsIgnoreCase("已撤单")) {
            return ENTRUST_STATUS_WITHDRAWED;
        } else if (name.equalsIgnoreCase("已成交")) {
            return ENTRUST_STATUS_DEALED;
        } else if (name.equalsIgnoreCase("未成交")) {
            return ENTRUST_STATUS_UNDEALED;
        }

        return ENTRUST_STATUS_UNKNOWN;
    }

    public static LiveTradeEntrustQueryDTO parseHengshengDTO(HengshengEntrustQryDTO data) {
        LiveTradeEntrustQueryDTO dto = new LiveTradeEntrustQueryDTO();

        for (int i = 0; i < data.data.size(); i ++) {
            HengshengEntrustQryData oneData = data.data.get(i);
            EntrustQueryDTO oneDto = new EntrustQueryDTO();
            oneDto.securityName = oneData.stock_name;
            oneDto.securityId = oneData.stock_code;
            oneDto.entrustName = oneData.entrust_bs==1?"买入":"卖出";
            oneDto.entrustPrice = oneData.entrust_price;
            oneDto.entrustAmount = (int)oneData.entrust_amount;
            oneDto.entrustDate = oneData.entrust_date;
            oneDto.entrustTime = oneData.entrust_time;
            oneDto.entrustStatus = parseHengshengEntrustStatusID(oneData.entrust_status);

            dto.positions.add(oneDto);
        }

        return dto;
    }

    private static int parseHengshengEntrustStatusID(int status) {
        if (status == 8) {
            return ENTRUST_STATUS_DEALED;
        } else if (status == 6) {
            return ENTRUST_STATUS_WITHDRAWED;
        } else {
            return ENTRUST_STATUS_UNDEALED;
        }
    }
}

