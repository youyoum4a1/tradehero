package com.androidth.general.fragments.onboarding.exchange;

import android.support.annotation.NonNull;
import com.androidth.general.common.api.SelectableDTO;
import com.androidth.general.api.market.ExchangeCompactDTO;

class SelectableExchangeDTO extends SelectableDTO<ExchangeCompactDTO>
{
    //<editor-fold desc="Constructors">
    SelectableExchangeDTO(@NonNull ExchangeCompactDTO value, boolean selected)
    {
        super(value, selected);
    }
    //</editor-fold>
}
