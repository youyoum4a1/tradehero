package com.tradehero.th.fragments.trending.filter;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;

public class TrendingFilterTypeBasicDTO extends TrendingFilterTypeDTO
{
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_basic_title;
    public static final int DEFAULT_ICON_RES_ID = 0;
    public static final int DEFAULT_DESCRIPTION_RES_ID = R.string.trending_filter_basic_description;
    public static final String TRACK_EVENT_SYMBOL = "Trending Securities";

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeBasicDTO()
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID);
    }

    public TrendingFilterTypeBasicDTO(ExchangeDTO exchangeDTO)
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID,
                exchangeDTO);
    }

    public TrendingFilterTypeBasicDTO(Bundle bundle)
    {
        super(bundle);
    }
    //</editor-fold>

    @Override public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypeGenericDTO(exchange);
    }

    @Override public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypeVolumeDTO(exchange);
    }

    @Override public TrendingSecurityListType getSecurityListType(String usableExchangeName, Integer page, Integer perPage)
    {
        return new TrendingBasicSecurityListType(usableExchangeName, page, perPage);
    }

    @Override public String getTrackEventCategory()
    {
        return TRACK_EVENT_SYMBOL;
    }

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putString(BUNDLE_KEY_CLASS_TYPE, TrendingFilterTypeBasicDTO.class.getName());
    }
}
