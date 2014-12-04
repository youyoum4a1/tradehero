package com.tradehero.th.fragments.trending.filter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;

public class TrendingFilterTypeGenericDTO extends TrendingFilterTypeDTO
{
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_all_title;
    public static final int DEFAULT_ICON_RES_ID = 0;
    public static final int DEFAULT_DESCRIPTION_RES_ID = R.string.trending_filter_all_description;
    public static final String TRACK_EVENT_SYMBOL = "All Securities";

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeGenericDTO(@NonNull Resources resources)
    {
        super(resources,
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID);
    }

    public TrendingFilterTypeGenericDTO(@NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID,
                exchangeCompactSpinnerDTO);
    }
    //</editor-fold>

    @Override protected boolean equals(@NonNull TrendingFilterTypeDTO other)
    {
        return super.equals(other) && other instanceof TrendingFilterTypeGenericDTO;
    }

    @NonNull @Override public TrendingFilterTypeDTO getByExchange(@NonNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        return new TrendingFilterTypeGenericDTO(exchangeCompactSpinnerDTO);
    }

    @Override @NonNull public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypePriceDTO(exchange);
    }

    @Override @NonNull public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypeBasicDTO(exchange);
    }

    @Override @NonNull public TrendingSecurityListType getSecurityListType(
            @Nullable String usableExchangeName,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        return new TrendingAllSecurityListType(usableExchangeName, page, perPage);
    }

    @Override @NonNull public String getTrackEventCategory()
    {
        return TRACK_EVENT_SYMBOL;
    }
}
