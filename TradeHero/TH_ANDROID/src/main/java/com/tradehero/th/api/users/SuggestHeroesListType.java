package com.ayondo.academy.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.market.ExchangeIntegerId;
import com.ayondo.academy.api.market.SectorId;

@Deprecated
public class SuggestHeroesListType extends UserListType
{
    //<editor-fold desc="Fields">
    @Nullable public final ExchangeIntegerId exchangeId;
    @Nullable public final SectorId sectorId;
    @Nullable public final Integer page;
    @Nullable public final Integer perPage;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public SuggestHeroesListType(
            @Nullable ExchangeIntegerId exchangeId,
            @Nullable SectorId sectorId,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        this.exchangeId = exchangeId;
        this.sectorId = sectorId;
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
        return (exchangeId == null ? 0 : exchangeId.hashCode()) ^
                (sectorId == null ? 0 : sectorId.hashCode()) ^
                (page == null ? 0 : page.hashCode()) ^
                (perPage == null ? 0 : perPage.hashCode());
    }

    @Override protected boolean equalFields(@NonNull UserListType other)
    {
        return other instanceof SuggestHeroesListType
                && equalFields((SuggestHeroesListType) other);
    }

    protected boolean equalFields(@NonNull SuggestHeroesListType other)
    {
        return exchangeId == null ? other.exchangeId == null : exchangeId.equals(other.exchangeId)
                && (sectorId == null ? other.sectorId == null : sectorId.equals(other.sectorId))
                && (page == null ? other.page == null : page.equals(other.page))
                && (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(@NonNull UserListType other)
    {
        if (!(other instanceof SuggestHeroesListType))
        {
            return SuggestHeroesListType.class.getName().compareTo(((Object) other).getClass().getName());
        }

        SuggestHeroesListType suggestHeroesListType = (SuggestHeroesListType) other;

        if (exchangeId == null)
        {
            if (suggestHeroesListType.exchangeId != null)
            {
                return 1;
            }
        }
        if (suggestHeroesListType.exchangeId == null)
        {
            return -1;
        }
        int exchangeCompare = exchangeId.compareTo(suggestHeroesListType.exchangeId);
        if (exchangeCompare != 0)
        {
            return exchangeCompare;
        }

        if (sectorId == null)
        {
            if (suggestHeroesListType.sectorId != null)
            {
                return 1;
            }
        }
        if (suggestHeroesListType.sectorId == null)
        {
            return -1;
        }
        int sectorCompare = sectorId.compareTo(suggestHeroesListType.sectorId);
        if (sectorCompare != 0)
        {
            return sectorCompare;
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
}
