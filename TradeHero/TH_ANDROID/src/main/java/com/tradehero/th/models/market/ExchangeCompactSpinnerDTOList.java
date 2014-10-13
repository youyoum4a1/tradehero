package com.tradehero.th.models.market;

import android.content.res.Resources;
import com.tradehero.th.api.market.BaseExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class ExchangeCompactSpinnerDTOList extends BaseExchangeCompactDTOList<ExchangeCompactSpinnerDTO>
{
    //<editor-fold desc="Constructors">
    public ExchangeCompactSpinnerDTOList(@NotNull Resources resources, @NotNull Collection<? extends ExchangeCompactDTO> exchangeCompactDTOs)
    {
        super();
        for (@NotNull ExchangeCompactDTO exchangeCompactDTO : exchangeCompactDTOs)
        {
            add(new ExchangeCompactSpinnerDTO(resources, exchangeCompactDTO));
        }
    }
    //</editor-fold>
}
