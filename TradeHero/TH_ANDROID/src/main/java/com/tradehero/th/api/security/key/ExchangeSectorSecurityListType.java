package com.tradehero.th.api.security.key;

import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.SectorCompactDTO;
import org.jetbrains.annotations.Nullable;

public class ExchangeSectorSecurityListType extends SecurityListType
{
    @Nullable public final String exchange;
    @Nullable public final String sector;

    //<editor-fold desc="Constructors">
    protected ExchangeSectorSecurityListType(
            @Nullable String exchange,
            @Nullable String sector,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(page, perPage);
        this.exchange = exchange;
        this.sector = sector;
    }

    public ExchangeSectorSecurityListType(
            @Nullable ExchangeCompactDTO exchange,
            @Nullable SectorCompactDTO sector,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        this(exchange == null ? null : exchange.name,
                sector == null ? null : sector.name,
                page, perPage);
    }
    //</editor-fold>
}
