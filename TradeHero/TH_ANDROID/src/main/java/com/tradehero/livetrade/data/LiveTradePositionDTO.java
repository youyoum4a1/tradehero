package com.tradehero.livetrade.data;

import com.tradehero.livetrade.data.subData.PositionDTO;

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
            oneDto.profit = tradeDataHelper.get(i, "profit", 0);
            oneDto.profitRatio = tradeDataHelper.get(i, "profit_ratio", 0);
            oneDto.marketValue = tradeDataHelper.get(i, "buy_money", 0);
            oneDto.price = tradeDataHelper.get(i, "cost_price", 0);
            oneDto.currentAmount = tradeDataHelper.get(i, "current_amt", 0);
            oneDto.enableAmount = tradeDataHelper.get(i, "enable_amt", 0);
            oneDto.marketName = tradeDataHelper.get(i, "market_name", "");
            dto.positions.add(oneDto);
        }

        return dto;
    }
}

