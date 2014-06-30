package com.tradehero.th.fragments.trending.filter;

import android.content.res.Resources;
import com.tradehero.thm.R;
import com.tradehero.th.api.security.key.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrendingFilterTypePriceDTO extends TrendingFilterTypeDTO
{
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_price_title;
    public static final int DEFAULT_ICON_RES_ID = R.drawable.ic_trending_price;
    public static final int DEFAULT_DESCRIPTION_RES_ID = R.string.trending_filter_price_description;
    public static final String TRACK_EVENT_SYMBOL = "Price Action";

    //<editor-fold desc="Constructors">
    public TrendingFilterTypePriceDTO(@NotNull Resources resources)
    {
        super(resources,
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID);
    }

    public TrendingFilterTypePriceDTO(@NotNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID,
                exchangeCompactSpinnerDTO);
    }
    //</editor-fold>

    @Override @NotNull public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypeVolumeDTO(exchange);
    }

    @Override @NotNull public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypeGenericDTO(exchange);
    }

    @Override @NotNull public TrendingSecurityListType getSecurityListType(
            @Nullable String usableExchangeName,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        return new TrendingPriceSecurityListType(usableExchangeName, page, perPage);
    }

    @Override @NotNull public String getTrackEventCategory()
    {
        return TRACK_EVENT_SYMBOL;
    }
}
