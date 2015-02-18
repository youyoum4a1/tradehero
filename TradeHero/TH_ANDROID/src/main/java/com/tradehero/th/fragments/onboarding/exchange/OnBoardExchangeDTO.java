package com.tradehero.th.fragments.onboarding.exchange;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;

class OnBoardExchangeDTO
{
    boolean selected;
    @NonNull final ExchangeCompactDTO exchange;
    @Nullable SecurityCompactDTOList topStocks;

    OnBoardExchangeDTO(
            boolean selected,
            @NonNull ExchangeCompactDTO exchange,
            @Nullable SecurityCompactDTOList topStocks)
    {
        this.selected = selected;
        this.exchange = exchange;
        this.topStocks = topStocks;
    }
    //</editor-fold>
}
