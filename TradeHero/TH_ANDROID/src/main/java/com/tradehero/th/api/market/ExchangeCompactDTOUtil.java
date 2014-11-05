package com.tradehero.th.api.market;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class ExchangeCompactDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public ExchangeCompactDTOUtil()
    {
        super();
    }
    //</editor-fold>

    @NonNull public ExchangeCompactDTOList filterAndOrderForTrending(
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
