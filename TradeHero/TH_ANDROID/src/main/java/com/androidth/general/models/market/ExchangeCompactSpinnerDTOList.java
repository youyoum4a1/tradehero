package com.androidth.general.models.market;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.api.market.BaseExchangeCompactDTOList;
import com.androidth.general.api.market.ExchangeCompactDTO;
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
