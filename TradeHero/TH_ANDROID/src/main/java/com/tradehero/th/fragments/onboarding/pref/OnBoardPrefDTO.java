package com.tradehero.th.fragments.onboarding.pref;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.SectorDTO;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;

public class OnBoardPrefDTO implements DTO
{
    @NonNull public final ExchangeCompactDTO preferredExchange;
    @NonNull public final SectorDTO preferredSector;

    //<editor-fold desc="Constructors">
    public OnBoardPrefDTO(
            @NonNull ExchangeCompactDTO preferredExchange,
            @NonNull SectorDTO preferredSector)
    {
        this.preferredExchange = preferredExchange;
        this.preferredSector = preferredSector;
    }
    //</editor-fold>

    @NonNull public ExchangeSectorSecurityListType createExchangeSectorSecurityListType()
    {
        return new ExchangeSectorSecurityListType(
                preferredExchange,
                preferredSector,
                1,
                null);
    }
}
