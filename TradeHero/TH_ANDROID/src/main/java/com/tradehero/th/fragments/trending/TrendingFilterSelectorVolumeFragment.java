package com.tradehero.th.fragments.trending;

import com.tradehero.th.R;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.api.security.TrendingVolumeSecurityListType;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 10:34 AM To change this template use File | Settings | File Templates. */
public class TrendingFilterSelectorVolumeFragment extends TrendingFilterSelectorFragment
{
    public static final int POSITION_IN_PAGER  = 1;

    @Override TrendingFilterTypeDTO getTrendingFilterTypeDTO()
    {
        return TrendingFilterTypeDTO.getVolume();
    }

    @Override public TrendingSecurityListType getTrendingSecurityListType(String exchangeName, Integer page, Integer perPage)
    {
        return sGetTrendingSecurityListType(exchangeName, page, perPage);
    }

    public static TrendingSecurityListType sGetTrendingSecurityListType(String exchangeName, Integer page, Integer perPage)
    {
        return new TrendingVolumeSecurityListType(exchangeName, page, perPage);
    }
}
