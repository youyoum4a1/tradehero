package com.ayondo.academy.api.security.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.market.ExchangeIntegerId;
import com.ayondo.academy.api.market.SectorId;
import java.util.Collections;
import java.util.List;

public class ExchangeSectorSecurityListTypeNew extends SecurityListType
{
    @Nullable public final List<ExchangeIntegerId> exchangeIds;
    @Nullable public final List<SectorId> sectorIds;

    //<editor-fold desc="Constructors">
    public ExchangeSectorSecurityListTypeNew(
            @Nullable List<ExchangeIntegerId> exchangeIds,
            @Nullable List<SectorId> sectorIds,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(page, perPage);
        this.exchangeIds = exchangeIds == null ? null : Collections.unmodifiableList(exchangeIds);
        this.sectorIds = sectorIds == null ? null : Collections.unmodifiableList(sectorIds);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        int exchangeIdsHash = 0;
        if (exchangeIds != null)
        {
            for (ExchangeIntegerId exchangeId : exchangeIds)
            {
                exchangeIdsHash ^= exchangeId.hashCode();
            }
        }
        int sectorIdsHash = 0;
        if (sectorIds != null)
        {
            for (SectorId sectorId : sectorIds)
            {
                sectorIdsHash ^= sectorId.hashCode();
            }
        }
        return super.hashCode() ^
                exchangeIdsHash ^
                sectorIdsHash;
    }

    @Override protected boolean equalFields(@NonNull SecurityListType other)
    {
        return other instanceof ExchangeSectorSecurityListTypeNew
                && equalFields((ExchangeSectorSecurityListTypeNew) other);
    }

    protected boolean equalFields(@NonNull ExchangeSectorSecurityListTypeNew other)
    {
        boolean exchangeIdsEqual = (exchangeIds != null || other.exchangeIds == null) &&
                (other.exchangeIds != null || exchangeIds == null);
        if (exchangeIdsEqual && exchangeIds != null)
        {
            for (ExchangeIntegerId exchangeId : exchangeIds)
            {
                exchangeIdsEqual &= other.exchangeIds.contains(exchangeId);
            }
            for (ExchangeIntegerId exchangeId : other.exchangeIds)
            {
                exchangeIdsEqual &= exchangeIds.contains(exchangeId);
            }
        }
        boolean sectorIdsEqual = (sectorIds != null || other.sectorIds == null) &&
                (other.sectorIds != null || sectorIds == null);
        if (sectorIdsEqual && sectorIds != null)
        {
            for (SectorId sectorId : sectorIds)
            {
                sectorIdsEqual &= other.sectorIds.contains(sectorId);
            }
            for (SectorId sectorId : other.sectorIds)
            {
                sectorIdsEqual &= sectorIds.contains(sectorId);
            }
        }
        return super.equalFields(other)
                && exchangeIdsEqual
                && sectorIdsEqual;
    }

    public String getCommaSeparatedExchangeIds()
    {
        if (exchangeIds == null)
        {
            return null;
        }
        char glue = ',';

        int length = exchangeIds.size();
        if (length == 0)
        {
            return null;
        }

        StringBuilder out = new StringBuilder();
        out.append(exchangeIds.get(0).key);
        for (int x = 1; x < length; ++x)
        {
            out.append(glue).append(exchangeIds.get(x).key);
        }
        return out.toString();
    }

    public String getCommaSeparatedSectorIds()
    {
        if (sectorIds == null)
        {
            return null;
        }
        char glue = ',';

        int length = sectorIds.size();
        if (length == 0)
        {
            return null;
        }

        StringBuilder out = new StringBuilder();
        out.append(sectorIds.get(0).key);
        for (int x = 1; x < length; ++x)
        {
            out.append(glue).append(sectorIds.get(x).key);
        }
        return out.toString();
    }
}
