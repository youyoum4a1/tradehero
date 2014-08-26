package com.tradehero.th.api.security.key;

import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.SectorCompactDTO;
import com.tradehero.th.api.market.SectorId;
import org.jetbrains.annotations.Nullable;

public class ExchangeSectorSecurityListType extends SecurityListType
{
    @Nullable public final ExchangeIntegerId exchangeId;
    @Nullable public final SectorId sectorId;

    //<editor-fold desc="Constructors">
    protected ExchangeSectorSecurityListType(
            @Nullable ExchangeIntegerId exchangeId,
            @Nullable SectorId sectorId,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(page, perPage);
        this.exchangeId = exchangeId;
        this.sectorId = sectorId;
    }

    public ExchangeSectorSecurityListType(
            @Nullable ExchangeCompactDTO exchange,
            @Nullable SectorCompactDTO sector,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        this(exchange == null ? null : exchange.getExchangeIntegerId(),
                sector == null ? null : sector.getSectorId(),
                page, perPage);
    }
    //</editor-fold>
}
