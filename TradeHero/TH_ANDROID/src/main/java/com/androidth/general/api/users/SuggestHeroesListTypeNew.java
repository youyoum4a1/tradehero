package com.androidth.general.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.market.ExchangeIntegerId;
import com.androidth.general.api.market.SectorId;
import java.util.Collections;
import java.util.List;

public class SuggestHeroesListTypeNew extends UserListType
{
    //<editor-fold desc="Fields">
    @Nullable public final List<ExchangeIntegerId> exchangeIds;
    @Nullable public final List<SectorId> sectorIds;
    @Nullable public final Integer page;
    @Nullable public final Integer perPage;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public SuggestHeroesListTypeNew(
            @Nullable List<ExchangeIntegerId> exchangeIds,
            @Nullable List<SectorId> sectorIds,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        this.exchangeIds = exchangeIds == null ? null : Collections.unmodifiableList(exchangeIds);
        this.sectorIds = sectorIds == null ? null : Collections.unmodifiableList(sectorIds);
        this.page = page;
        this.perPage = perPage;
    }
    //</editor-fold>

    @Override @Nullable public Integer getPage()
    {
        return page;
    }

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
        return exchangeIdsHash ^
                sectorIdsHash ^
                (page == null ? 0 : page.hashCode()) ^
                (perPage == null ? 0 : perPage.hashCode());
    }

    @Override protected boolean equalFields(@NonNull UserListType other)
    {
        return other instanceof SuggestHeroesListTypeNew
                && equalFields((SuggestHeroesListTypeNew) other);
    }

    protected boolean equalFields(@NonNull SuggestHeroesListTypeNew other)
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
        return exchangeIdsEqual
                && sectorIdsEqual
                && (page == null ? other.page == null : page.equals(other.page))
                && (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(@NonNull UserListType other)
    {
        if (!(other instanceof SuggestHeroesListTypeNew))
        {
            return SuggestHeroesListTypeNew.class.getName().compareTo(((Object) other).getClass().getName());
        }

        SuggestHeroesListTypeNew suggestHeroesListType = (SuggestHeroesListTypeNew) other;

        if (exchangeIds == null)
        {
            if (suggestHeroesListType.exchangeIds != null)
            {
                return 1;
            }
        }
        if (suggestHeroesListType.exchangeIds == null)
        {
            return -1;
        }

        if (sectorIds == null)
        {
            if (suggestHeroesListType.sectorIds != null)
            {
                return 1;
            }
        }
        if (suggestHeroesListType.sectorIds == null)
        {
            return -1;
        }

        if (page == null)
        {
            if (suggestHeroesListType.page != null)
            {
                return 1;
            }
        }
        if (suggestHeroesListType.page == null)
        {
            return -1;
        }
        int pageCompare = page.compareTo(suggestHeroesListType.page);
        if (pageCompare != 0)
        {
            return pageCompare;
        }

        if (perPage == null)
        {
            if (suggestHeroesListType.perPage != null)
            {
                return 1;
            }
        }
        if (suggestHeroesListType.perPage == null)
        {
            return -1;
        }
        return perPage.compareTo(suggestHeroesListType.perPage);
    }
    //</editor-fold>

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
