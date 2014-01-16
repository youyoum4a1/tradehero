package com.tradehero.th.fragments.trending.filter;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;

/**
 * Created by xavier on 1/15/14.
 */
public class TrendingFilterTypeBasicDTO extends TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypeBasicDTO.class.getSimpleName();

    public TrendingFilterTypeBasicDTO()
    {
        super(true, true,
                R.string.trending_filter_basic_title, 0,
                R.string.trending_filter_basic_description);
    }

    public TrendingFilterTypeBasicDTO(ExchangeDTO exchangeDTO)
    {
        super(true, true,
                R.string.trending_filter_basic_title, 0,
                R.string.trending_filter_basic_description,
                exchangeDTO);
    }

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

}
