package com.tradehero.th.fragments.trending.filter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;

abstract public class TrendingFilterTypeDTO
{
    public static final String BUNDLE_KEY_CLASS_TYPE = TrendingFilterTypeDTO.class.getName() + ".classType";
    public static final String BUNDLE_KEY_TITLE_RES_ID = TrendingFilterTypeDTO.class.getName() + ".titleResId";
    public static final String BUNDLE_KEY_TITLE_ICON_RES_ID = TrendingFilterTypeDTO.class.getName() + ".iconResId";
    public static final String BUNDLE_KEY_DESCRIPTION_RES_ID = TrendingFilterTypeDTO.class.getName() + ".descriptionResId";
    public static final String BUNDLE_KEY_EXCHANGE = TrendingFilterTypeDTO.class.getName() + ".exchange";

    @StringRes public final int titleResId;
    @DrawableRes public final int titleIconResId;
    @StringRes public final int descriptionResId;

    @NonNull public final ExchangeCompactSpinnerDTO exchange;

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeDTO(
            @NonNull Resources resources,
            @StringRes int titleResId,
            @DrawableRes int titleIconResId,
            @StringRes int descriptionResId)
    {
        this(titleResId,
                titleIconResId,
                descriptionResId,
                new ExchangeCompactSpinnerDTO(resources));
    }

    public TrendingFilterTypeDTO(
            @StringRes int titleResId,
            @DrawableRes int titleIconResId,
            @StringRes int descriptionResId,
            @NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        this.titleResId = titleResId;
        this.titleIconResId = titleIconResId;
        this.descriptionResId = descriptionResId;
        this.exchange = exchangeCompactSpinnerDTO;
    }

    public TrendingFilterTypeDTO(@NonNull Resources resources, @NonNull Bundle bundle)
    {
        this.titleResId = bundle.getInt(BUNDLE_KEY_TITLE_RES_ID);
        this.titleIconResId = bundle.getInt(BUNDLE_KEY_TITLE_ICON_RES_ID);
        this.descriptionResId = bundle.getInt(BUNDLE_KEY_DESCRIPTION_RES_ID);
        this.exchange = new ExchangeCompactSpinnerDTO(resources, bundle.getBundle(BUNDLE_KEY_EXCHANGE));
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
