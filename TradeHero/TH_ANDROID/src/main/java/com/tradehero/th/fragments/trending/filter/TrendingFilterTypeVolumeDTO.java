package com.tradehero.th.fragments.trending.filter;

import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.api.security.TrendingVolumeSecurityListType;

/**
 * Created by xavier on 1/15/14.
 */
public class TrendingFilterTypeVolumeDTO extends TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypeVolumeDTO.class.getSimpleName();

    public TrendingFilterTypeVolumeDTO()
    {
        super(true, true,
                R.string.trending_filter_volume_title, R.drawable.ic_trending_volume,
                R.string.trending_filter_volume_description);
    }

    public TrendingFilterTypeVolumeDTO(ExchangeDTO exchangeDTO)
    {
        super(true, true,
                R.string.trending_filter_volume_title, R.drawable.ic_trending_volume,
                R.string.trending_filter_volume_description,
                exchangeDTO);
    }

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

}
