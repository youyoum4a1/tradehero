package com.ayondo.academy.models.market;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.market.BaseExchangeCompactDTOList;
import com.ayondo.academy.api.market.ExchangeCompactDTO;
import java.util.Collection;

public class ExchangeCompactSpinnerDTOList extends BaseExchangeCompactDTOList<ExchangeCompactSpinnerDTO>
{
    //<editor-fold desc="Constructors">
    public ExchangeCompactSpinnerDTOList(@NonNull Resources resources, @NonNull Collection<? extends ExchangeCompactDTO> exchangeCompactDTOs)
    {
        super();
        for (ExchangeCompactDTO exchangeCompactDTO : exchangeCompactDTOs)
        {
            add(new ExchangeCompactSpinnerDTO(resources, exchangeCompactDTO));
        }
    }
    //</editor-fold>
}
