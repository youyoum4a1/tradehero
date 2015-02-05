package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import java.util.Comparator;
import java.util.List;
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
}
