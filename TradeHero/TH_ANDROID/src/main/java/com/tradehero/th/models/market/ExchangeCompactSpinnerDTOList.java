package com.tradehero.th.models.market;

import android.content.res.Resources;
import com.tradehero.th.api.market.BaseExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import java.util.Collection;
import android.support.annotation.NonNull;

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
