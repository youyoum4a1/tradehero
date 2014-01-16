package com.tradehero.th.fragments.trending.filter;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.api.security.TrendingAllSecurityListType;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;

/**
 * Created by xavier on 1/15/14.
 */
public class TrendingFilterTypeGenericDTO extends TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypeGenericDTO.class.getSimpleName();

    public TrendingFilterTypeGenericDTO()
    {
        super(true, true,
                R.string.trending_filter_all_title, 0,
                R.string.trending_filter_all_description);
    }

    public TrendingFilterTypeGenericDTO(ExchangeDTO exchangeDTO)
    {
        super(true, true,
                R.string.trending_filter_all_title, 0,
                R.string.trending_filter_all_description,
                exchangeDTO);
    }

    @Override public TrendingFilterTypeDTO getPrevious()
    {
        return new TrendingFilterTypePriceDTO(exchange);
    }

    @Override public TrendingFilterTypeDTO getNext()
    {
        return new TrendingFilterTypeBasicDTO(exchange);
    }

    @Override public TrendingSecurityListType getSecurityListType(String usableExchangeName, Integer page, Integer perPage)
    {
        return new TrendingAllSecurityListType(usableExchangeName, page, perPage);
    }

}
