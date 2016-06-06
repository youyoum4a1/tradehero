package com.androidth.general.fragments.trending.filter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.R;
import com.androidth.general.api.security.key.TrendingBasicSecurityListType;
import com.androidth.general.api.security.key.TrendingSecurityListType;
import com.androidth.general.models.market.ExchangeCompactSpinnerDTO;

public class TrendingFilterTypeBasicDTO extends TrendingFilterTypeDTO
{
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_basic_title;
    public static final String TRACK_EVENT_SYMBOL = "Trending Securities";

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeBasicDTO(@NonNull Resources resources)
    {
        super(resources, DEFAULT_TITLE_RES_ID);
    }

    public TrendingFilterTypeBasicDTO(@NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        super(DEFAULT_TITLE_RES_ID, exchangeCompactSpinnerDTO);
    }
    //</editor-fold>

    @Override protected boolean equals(@NonNull TrendingFilterTypeDTO other)
    {
        return super.equals(other) && other instanceof TrendingFilterTypeBasicDTO;
    }

    @NonNull @Override public TrendingFilterTypeDTO getByExchange(@NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        return new TrendingFilterTypeBasicDTO(exchangeCompactSpinnerDTO);
    }

    @Override @NonNull public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypeGenericDTO(exchange);
    }

    @Override @NonNull public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypeVolumeDTO(exchange);
    }

    @Override @NonNull public TrendingSecurityListType getSecurityListType(
            @Nullable String usableExchangeName,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        return new TrendingBasicSecurityListType(usableExchangeName, page, perPage);
    }

    @Override @NonNull public String getTrackEventCategory()
    {
        return TRACK_EVENT_SYMBOL;
    }
}
