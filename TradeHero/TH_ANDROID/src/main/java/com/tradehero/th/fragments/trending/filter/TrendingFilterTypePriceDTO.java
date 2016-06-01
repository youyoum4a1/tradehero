package com.ayondo.academy.fragments.trending.filter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.R;
import com.ayondo.academy.api.security.key.TrendingPriceSecurityListType;
import com.ayondo.academy.api.security.key.TrendingSecurityListType;
import com.ayondo.academy.models.market.ExchangeCompactSpinnerDTO;

public class TrendingFilterTypePriceDTO extends TrendingFilterTypeDTO
{
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_price_title;
    public static final String TRACK_EVENT_SYMBOL = "Price Action";

    //<editor-fold desc="Constructors">
    public TrendingFilterTypePriceDTO(@NonNull Resources resources)
    {
        super(resources, DEFAULT_TITLE_RES_ID);
    }

    public TrendingFilterTypePriceDTO(@NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        super(DEFAULT_TITLE_RES_ID, exchangeCompactSpinnerDTO);
    }
    //</editor-fold>

    @Override protected boolean equals(@NonNull TrendingFilterTypeDTO other)
    {
        return super.equals(other) && other instanceof TrendingFilterTypePriceDTO;
    }

    @NonNull @Override public TrendingFilterTypeDTO getByExchange(@NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        return new TrendingFilterTypePriceDTO(exchangeCompactSpinnerDTO);
    }

    @Override @NonNull public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypeVolumeDTO(exchange);
    }

    @Override @NonNull public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypeGenericDTO(exchange);
    }

    @Override @NonNull public TrendingSecurityListType getSecurityListType(
            @Nullable String usableExchangeName,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        return new TrendingPriceSecurityListType(usableExchangeName, page, perPage);
    }

    @Override @NonNull public String getTrackEventCategory()
    {
        return TRACK_EVENT_SYMBOL;
    }
}
