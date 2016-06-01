package com.ayondo.academy.fragments.onboarding.exchange;

import android.support.annotation.NonNull;
import com.tradehero.common.api.SelectableDTO;
import com.ayondo.academy.api.market.ExchangeCompactDTO;

class SelectableExchangeDTO extends SelectableDTO<ExchangeCompactDTO>
{
    //<editor-fold desc="Constructors">
    SelectableExchangeDTO(@NonNull ExchangeCompactDTO value, boolean selected)
    {
        super(value, selected);
    }
    //</editor-fold>
}
