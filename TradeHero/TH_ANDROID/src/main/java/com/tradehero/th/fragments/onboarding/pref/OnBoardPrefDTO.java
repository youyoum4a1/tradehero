package com.tradehero.th.fragments.onboarding.pref;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.SectorCompactDTO;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import org.jetbrains.annotations.NotNull;

public class OnBoardPrefDTO implements DTO
{
    @NotNull public final ExchangeCompactDTO preferredExchange;
    @NotNull public final SectorCompactDTO preferredSector;

    //<editor-fold desc="Constructors">
    public OnBoardPrefDTO(
            @NotNull ExchangeCompactDTO preferredExchange,
            @NotNull SectorCompactDTO preferredSector)
    {
        this.preferredExchange = preferredExchange;
        this.preferredSector = preferredSector;
    }
    //</editor-fold>

    @NotNull public ExchangeSectorSecurityListType createExchangeSectorSecurityListType()
    {
        return new ExchangeSectorSecurityListType(
                preferredExchange,
                preferredSector,
                1,
                null);
    }
}
