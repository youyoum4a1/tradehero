package com.tradehero.th.models.market;

import android.content.res.Resources;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class ExchangeCompactSpinnerDTOList extends ArrayList<ExchangeCompactSpinnerDTO>
{
    //<editor-fold desc="Constructors">
    public ExchangeCompactSpinnerDTOList(@NotNull Resources resources, @NotNull Collection<? extends ExchangeCompactDTO> exchangeCompactDTOs)
    {
        super();
        add(new ExchangeCompactSpinnerDTO(resources));
        for (@NotNull ExchangeCompactDTO exchangeCompactDTO : exchangeCompactDTOs)
        {
            add(new ExchangeCompactSpinnerDTO(resources, exchangeCompactDTO));
        }
    }
    //</editor-fold>
}
