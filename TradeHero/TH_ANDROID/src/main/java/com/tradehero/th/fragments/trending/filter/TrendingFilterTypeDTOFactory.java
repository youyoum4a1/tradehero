package com.tradehero.th.fragments.trending.filter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.fragments.trending.TrendingStockSortType;

public class TrendingFilterTypeDTOFactory
{
    @NonNull public static TrendingFilterTypeDTO create(
            @NonNull TrendingStockSortType type,
            @NonNull Resources resources)
    {
        switch (type)
        {
            case Trending:
                return new TrendingFilterTypeBasicDTO(resources);

            case Price:
                return new TrendingFilterTypePriceDTO(resources);

            case Volume:
                return new TrendingFilterTypeVolumeDTO(resources);

            default:
                return new TrendingFilterTypeGenericDTO(resources);
        }
    }
}
