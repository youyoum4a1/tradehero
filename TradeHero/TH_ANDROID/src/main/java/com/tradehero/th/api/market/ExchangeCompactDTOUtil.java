package com.tradehero.th.api.market;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ExchangeCompactDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public ExchangeCompactDTOUtil()
    {
        super();
    }
    //</editor-fold>

    @NotNull public ExchangeCompactDTOList filterAndOrderForTrending(
            @NotNull List<? extends ExchangeCompactDTO> rough,
            @NotNull Comparator<ExchangeCompactDTO> comparator)
    {
        TreeSet<ExchangeCompactDTO> filtered = new TreeSet<>(comparator);
        for (@NotNull ExchangeCompactDTO exchangeCompactDTO : rough)
        {
            if (exchangeCompactDTO.isIncludedInTrending)
            {
                filtered.add(exchangeCompactDTO);
            }
        }
        return new ExchangeCompactDTOList(filtered);
    }
}
