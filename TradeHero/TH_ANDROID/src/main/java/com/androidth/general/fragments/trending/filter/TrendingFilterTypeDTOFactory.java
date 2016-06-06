package com.androidth.general.fragments.trending.filter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.fragments.trending.TrendingStockTabType;

public class TrendingFilterTypeDTOFactory
{
    @NonNull public static TrendingFilterTypeDTO create(
            @NonNull TrendingStockTabType type,
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
