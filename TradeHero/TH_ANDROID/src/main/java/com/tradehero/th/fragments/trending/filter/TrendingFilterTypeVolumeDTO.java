package com.tradehero.th.fragments.trending.filter;

import android.content.res.Resources;
import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.security.key.TrendingVolumeSecurityListType;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrendingFilterTypeVolumeDTO extends TrendingFilterTypeDTO
{
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_volume_title;
    public static final int DEFAULT_ICON_RES_ID = R.drawable.ic_trending_volume;
    public static final int DEFAULT_DESCRIPTION_RES_ID = R.string.trending_filter_volume_description;
    public static final String TRACK_EVENT_SYMBOL = "Unusual Volumes";

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeVolumeDTO(@NotNull Resources resources)
    {
        super(resources,
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID);
    }

    public TrendingFilterTypeVolumeDTO(@NotNull ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO)
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID,
                exchangeCompactSpinnerDTO);
    }

    public TrendingFilterTypeVolumeDTO(@NotNull Resources resources, @NotNull Bundle bundle)
    {
        super(resources, bundle);
    }
    //</editor-fold>

    @Override @NotNull public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypeBasicDTO(exchange);
    }

    @Override @NotNull public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypePriceDTO(exchange);
    }

    @Override @NotNull public TrendingSecurityListType getSecurityListType(
            @Nullable String usableExchangeName,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        return new TrendingVolumeSecurityListType(usableExchangeName, page, perPage);
    }

    @Override @NotNull public String getTrackEventCategory()
    {
        return TRACK_EVENT_SYMBOL;
    }

    @Override protected void putParameters(@NotNull Bundle args)
    {
        super.putParameters(args);
        args.putString(BUNDLE_KEY_CLASS_TYPE, TrendingFilterTypeVolumeDTO.class.getName());
    }
}
