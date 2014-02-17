package com.tradehero.th.fragments.trending.filter;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.security.key.TrendingVolumeSecurityListType;

/**
 * Created by xavier on 1/15/14.
 */
public class TrendingFilterTypeVolumeDTO extends TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypeVolumeDTO.class.getSimpleName();
    public static final int DEFAULT_TITLE_RES_ID = R.string.trending_filter_volume_title;
    public static final int DEFAULT_ICON_RES_ID = R.drawable.ic_trending_volume;
    public static final int DEFAULT_DESCRIPTION_RES_ID = R.string.trending_filter_volume_description;

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeVolumeDTO()
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID);
    }

    public TrendingFilterTypeVolumeDTO(ExchangeDTO exchangeDTO)
    {
        super(
                DEFAULT_TITLE_RES_ID,
                DEFAULT_ICON_RES_ID,
                DEFAULT_DESCRIPTION_RES_ID,
                exchangeDTO);
    }

    public TrendingFilterTypeVolumeDTO(Bundle bundle)
    {
        super(bundle);
    }
    //</editor-fold>

    @Override public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypeBasicDTO(exchange);
    }

    @Override public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypePriceDTO(exchange);
    }

    @Override public TrendingSecurityListType getSecurityListType(String usableExchangeName, Integer page, Integer perPage)
    {
        return new TrendingVolumeSecurityListType(usableExchangeName, page, perPage);
    }

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putString(BUNDLE_KEY_CLASS_TYPE, TrendingFilterTypeVolumeDTO.class.getName());
    }
}
