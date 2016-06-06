package com.androidth.general.fragments.trending.filter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.androidth.general.api.security.key.TrendingSecurityListType;
import com.androidth.general.models.market.ExchangeCompactSpinnerDTO;

abstract public class TrendingFilterTypeDTO
{
    @StringRes public final int titleResId;
    @NonNull public final ExchangeCompactSpinnerDTO exchange;

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeDTO(
            @NonNull Resources resources,
            @StringRes int titleResId)
    {
        this(titleResId, new ExchangeCompactSpinnerDTO(resources));
    }

    public TrendingFilterTypeDTO(
            @StringRes int titleResId,
            @NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        this.titleResId = titleResId;
        this.exchange = exchangeCompactSpinnerDTO;
    }
    //</editor-fold>

    @NonNull public TrendingSecurityListType getSecurityListType(@Nullable Integer page, @Nullable Integer perPage)
    {
        return getSecurityListType(exchange.getApiName(), page, perPage);
    }

    @Override public int hashCode()
    {
        return exchange.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        return (o instanceof TrendingFilterTypeDTO) && equals((TrendingFilterTypeDTO) o);
    }

    protected boolean equals(@NonNull TrendingFilterTypeDTO other)
    {
        return other.exchange.equals(exchange);
    }

    @NonNull abstract public TrendingFilterTypeDTO getByExchange(@NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO);
    @NonNull abstract public TrendingFilterTypeDTO getPrevious();
    @NonNull abstract public TrendingFilterTypeDTO getNext();
    @NonNull abstract public TrendingSecurityListType getSecurityListType(@Nullable String usableExchangeName, @Nullable Integer page, @Nullable Integer perPage);
    @NonNull abstract public String getTrackEventCategory();
}
