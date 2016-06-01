package com.ayondo.academy.api.security.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.market.ExchangeIntegerId;
import com.ayondo.academy.api.market.SectorId;

@Deprecated
public class ExchangeSectorSecurityListType extends SecurityListType
{
    @Nullable public final ExchangeIntegerId exchangeId;
    @Nullable public final SectorId sectorId;

    //<editor-fold desc="Constructors">
    public ExchangeSectorSecurityListType(
            @Nullable ExchangeIntegerId exchangeId,
            @Nullable SectorId sectorId,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(page, perPage);
        this.exchangeId = exchangeId;
        this.sectorId = sectorId;
    }
    //</editor-fold>

    @Override protected boolean equalFields(@NonNull SecurityListType other)
    {
        return other instanceof ExchangeSectorSecurityListType
                && equalFields((ExchangeSectorSecurityListType) other);
    }

    protected boolean equalFields(@NonNull ExchangeSectorSecurityListType other)
    {
        return super.equalFields(other)
                && (exchangeId == null ? other.exchangeId == null : exchangeId.equals(other.exchangeId))
                && (sectorId == null ? other.sectorId == null : sectorId.equals(other.sectorId));
    }
}
