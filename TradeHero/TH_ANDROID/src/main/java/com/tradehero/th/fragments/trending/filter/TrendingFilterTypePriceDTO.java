package com.tradehero.th.fragments.trending.filter;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;

/**
 * Created by xavier on 1/15/14.
 */
public class TrendingFilterTypePriceDTO extends TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypePriceDTO.class.getSimpleName();

    public TrendingFilterTypePriceDTO()
    {
        super(true, true,
                R.string.trending_filter_price_title, R.drawable.ic_trending_price,
                R.string.trending_filter_price_description);
    }

    public TrendingFilterTypePriceDTO(ExchangeDTO exchangeDTO)
    {
        super(true, true,
                R.string.trending_filter_price_title, R.drawable.ic_trending_price,
                R.string.trending_filter_price_description,
                exchangeDTO);
    }

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

}
