package com.tradehero.th.fragments.trending.filter;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.security.key.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;

/**
 * Created by xavier on 1/15/14.
 */
public class TrendingFilterTypePriceDTO extends TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypePriceDTO.class.getSimpleName();
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_price_title;
    public static final int DEFAULT_ICON_RES_ID = R.drawable.ic_trending_price;
    public static final int DEFAULT_DESCRIPTION_RES_ID = R.string.trending_filter_price_description;
    public static final String TRACK_EVENT_SYMBOL = "Price Action";

    //<editor-fold desc="Constructors">
    public TrendingFilterTypePriceDTO()
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID);
    }

    public TrendingFilterTypePriceDTO(ExchangeDTO exchangeDTO)
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID,
                exchangeDTO);
    }

    public TrendingFilterTypePriceDTO(Bundle bundle)
    {
        super(bundle);
    }
    //</editor-fold>

    @Override public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypeVolumeDTO(exchange);
    }

    @Override public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypeGenericDTO(exchange);
    }

    @Override public TrendingSecurityListType getSecurityListType(String usableExchangeName, Integer page, Integer perPage)
    {
        return new TrendingPriceSecurityListType(usableExchangeName, page, perPage);
    }

    @Override public String getTrackEventCategory()
    {
        return TRACK_EVENT_SYMBOL;
    }

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putString(BUNDLE_KEY_CLASS_TYPE, TrendingFilterTypePriceDTO.class.getName());
    }
}
