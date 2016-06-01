package com.ayondo.academy.api.market;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class ExchangeCompactDTOUtil
{
    @NonNull public static ExchangeCompactDTOList filterAndOrderForTrending(
            @NonNull List<? extends ExchangeCompactDTO> rough,
            @NonNull Comparator<ExchangeCompactDTO> comparator)
    {
        TreeSet<ExchangeCompactDTO> filtered = new TreeSet<>(comparator);
        for (ExchangeCompactDTO exchangeCompactDTO : rough)
        {
            if (exchangeCompactDTO.isIncludedInTrending)
            {
                filtered.add(exchangeCompactDTO);
            }
        }
        return new ExchangeCompactDTOList(filtered);
    }

    @NonNull public static Map<MarketRegion, List<ExchangeIntegerId>> filePerRegion(
            @NonNull List<? extends ExchangeCompactDTO> list)
    {
        Map<MarketRegion, List<ExchangeIntegerId>> filed = new HashMap<>();
        for (ExchangeCompactDTO item : list)
        {
            if (filed.get(item.region) == null)
            {
                filed.put(item.region, new ArrayList<ExchangeIntegerId>());
            }
            filed.get(item.region).add(item.getExchangeIntegerId());
        }
        return filed;
    }
}
