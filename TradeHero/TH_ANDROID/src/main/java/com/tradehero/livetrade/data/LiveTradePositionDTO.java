package com.tradehero.livetrade.data;

import com.tradehero.livetrade.data.subData.PositionDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengPositionDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengPositionData;

import java.util.ArrayList;
import java.util.List;

import cn.htsec.data.pkg.trade.TradeDataHelper;

/**
 * Created by Sam on 15/8/27.
 */
public class LiveTradePositionDTO {
    public List<PositionDTO> positions;

    public LiveTradePositionDTO() {
        this.positions = new ArrayList<>();
    }

    public static LiveTradePositionDTO parseHaitongDTO(TradeDataHelper tradeDataHelper) {
        LiveTradePositionDTO dto = new LiveTradePositionDTO();

        int rowCount = tradeDataHelper.getRowCount();
        for(int i = 0; i < rowCount; i++) {
            PositionDTO oneDto = new PositionDTO();
            oneDto.stockName = tradeDataHelper.get(i, "sec_name", "");
            oneDto.stockCode = tradeDataHelper.get(i, "sec_code", "");
            oneDto.profit = tradeDataHelper.get(i, "profit", 0.0f);
            oneDto.profitRatio = tradeDataHelper.get(i, "profit_ratio", 0.0f);
            oneDto.marketValue = tradeDataHelper.get(i, "buy_money", 0.0f);
            oneDto.price = tradeDataHelper.get(i, "cost_price", 0.0f);
            oneDto.currentAmount = tradeDataHelper.get(i, "current_amt", 0.0f);
            oneDto.enableAmount = tradeDataHelper.get(i, "enable_amt", 0.0f);
            oneDto.marketName = tradeDataHelper.get(i, "market_name", "");
            dto.positions.add(oneDto);
        }

        return dto;
    }

    public static LiveTradePositionDTO parseHengshengDTO(HengshengPositionDTO hengshengDTO) {
        LiveTradePositionDTO dto = new LiveTradePositionDTO();

        for (int i = 0; i < hengshengDTO.data.size(); i ++) {
            HengshengPositionData oneData = hengshengDTO.data.get(i);
            PositionDTO oneDTO = new PositionDTO();

            oneDTO.stockName = oneData.stock_name;
            oneDTO.stockCode = oneData.stock_code;
            oneDTO.profit = oneData.income_balance;
            oneDTO.profitRatio = oneData.income_balance / (oneData.market_value - oneData.income_balance) * 100;
            oneDTO.marketValue = oneData.market_value;
            oneDTO.price = oneData.last_price;
            oneDTO.currentAmount = oneData.current_amount;
            oneDTO.enableAmount = oneData.enable_amount;
            oneDTO.marketName = "沪A";       // TODO

            dto.positions.add(oneDTO);
        }

        return dto;
    }
}

