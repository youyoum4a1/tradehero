package com.tradehero.th.fragments.onboarding.exchange;

import android.support.annotation.NonNull;
import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.api.market.ExchangeDTO;

class SelectableExchangeDTO extends SelectableDTO<ExchangeDTO>
{
    //<editor-fold desc="Constructors">
    SelectableExchangeDTO(@NonNull ExchangeDTO value, boolean selected)
    {
        super(value, selected);
    }
    //</editor-fold>
}
