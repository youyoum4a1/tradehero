package com.tradehero.livetrade.data;

import com.tradehero.livetrade.data.subData.DealQueryDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengBusinessQryDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengBusinessQryData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.htsec.data.pkg.trade.TradeDataHelper;
import timber.log.Timber;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class LiveTradeDealQueryDTO {

    public List<DealQueryDTO> positions;

    public LiveTradeDealQueryDTO() {
        positions = new ArrayList();
    }

    public static LiveTradeDealQueryDTO parseHaitiongData(TradeDataHelper helper) {
        LiveTradeDealQueryDTO dtos = new LiveTradeDealQueryDTO();

        int rowCount = helper.getRowCount();
        int responseCode = helper.getResponseCode();
        String responseMsg  = helper.getResponseMsg();

        int resultCode = helper.getResultCode();
        String resultMsg = helper.getResultMsg();

        int startPosition = helper.getStartPosition();

        StringBuffer sb = new StringBuffer();
        sb.append("响应Code:"+responseCode+"\n");
        sb.append("响应Msg:"+responseMsg+"\n");
        sb.append("结果Code:"+resultCode+"\n");
        sb.append("结果Msg:"+resultMsg+"\n");
        sb.append("起始位置:"+startPosition+"\n");
        sb.append("结果行数:"+rowCount+"\n");

        List<String> keys = helper.getKeys();
        for(int i = 0; i < rowCount; i++)
        {
            sb.append(i+",");
            DealQueryDTO dto = new DealQueryDTO();
            for(int j = 0; j < keys.size(); j++)
            {
                String key = keys.get(j);
                sb.append(key+":"+helper.get(i, key, null));
                if(j != keys.size() - 1)
                {
                    sb.append("  ");
                }
                if (key.equalsIgnoreCase("sec_name")) {
                    dto.securityName = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("sec_code")) {
                    dto.securityId = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("entrust_name")) {
                    dto.entrustName = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("business_price")) {
                    dto.businessPrice = helper.get(i, key, 0.0f);
                } else if (key.equalsIgnoreCase("business_amt")) {
                    dto.businessAmount = helper.get(i, key, 0);
                } else if (key.equalsIgnoreCase("business_date")) {
                    dto.businessDate = helper.get(i, key, null);
                } else if (key.equalsIgnoreCase("business_time")) {
                    dto.businessTime = helper.get(i, key, null);
                }
            }
            sb.append("\n");
            dtos.positions.add(dto);
        }

        Timber.d("lyl " + sb.toString());
        return dtos;
    }

    public static LiveTradeDealQueryDTO parseHengshengDTO(HengshengBusinessQryDTO data) {
        LiveTradeDealQueryDTO dto = new LiveTradeDealQueryDTO();

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");

        Calendar cal = Calendar.getInstance();// 取当前日期。
        String today = format.format(cal.getTime());

        for (int i = 0; i < data.data.size(); i ++) {
            HengshengBusinessQryData oneData = data.data.get(i);
            DealQueryDTO oneDto = new DealQueryDTO();

            oneDto.securityName = oneData.stock_name;
            oneDto.securityId = oneData.stock_code;
            oneDto.entrustName = oneData.entrust_bs==1?"买入":"卖出";
            oneDto.businessPrice = oneData.business_price;
            oneDto.businessAmount = (int)oneData.business_amount;
            oneDto.businessDate = today;
            oneDto.businessTime = oneData.business_time;

            dto.positions.add(oneDto);
        }

        return dto;
    }
}

