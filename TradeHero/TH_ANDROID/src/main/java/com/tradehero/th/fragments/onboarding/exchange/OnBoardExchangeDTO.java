package com.tradehero.th.fragments.onboarding.exchange;

import android.support.annotation.NonNull;
import com.tradehero.th.api.market.ExchangeDTO;

class OnBoardExchangeDTO
{
    boolean selected;
    @NonNull final ExchangeDTO exchange;

    OnBoardExchangeDTO(
            boolean selected,
            @NonNull ExchangeDTO exchange)
    {
        this.selected = selected;
        this.exchange = exchange;
    }
    //</editor-fold>
}
