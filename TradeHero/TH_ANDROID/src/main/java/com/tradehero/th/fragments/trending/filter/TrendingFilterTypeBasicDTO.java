package com.tradehero.th.fragments.trending.filter;

import android.content.res.Resources;
import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrendingFilterTypeBasicDTO extends TrendingFilterTypeDTO
{
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_basic_title;
    public static final int DEFAULT_ICON_RES_ID = 0;
    public static final int DEFAULT_DESCRIPTION_RES_ID = R.string.trending_filter_basic_description;
    public static final String TRACK_EVENT_SYMBOL = "Trending Securities";

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeBasicDTO(@NotNull Resources resources)
    {
        super(resources,
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID);
    }

    public TrendingFilterTypeBasicDTO(@NotNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID,
                exchangeCompactSpinnerDTO);
    }

    public TrendingFilterTypeBasicDTO(@NotNull Resources resources, @NotNull Bundle bundle)
    {
        super(resources, bundle);
    }
    //</editor-fold>

    @Override @NotNull public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypeGenericDTO(exchange);
    }

    @Override @NotNull public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypeVolumeDTO(exchange);
    }

    @Override @NotNull public TrendingSecurityListType getSecurityListType(
            @Nullable String usableExchangeName,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        return new TrendingBasicSecurityListType(usableExchangeName, page, perPage);
    }

    @Override @NotNull public String getTrackEventCategory()
    {
        return TRACK_EVENT_SYMBOL;
    }

    @Override protected void putParameters(@NotNull Bundle args)
    {
        super.putParameters(args);
        args.putString(BUNDLE_KEY_CLASS_TYPE, TrendingFilterTypeBasicDTO.class.getName());
    }
}
